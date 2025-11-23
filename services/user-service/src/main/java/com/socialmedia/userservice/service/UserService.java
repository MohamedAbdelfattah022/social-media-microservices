package com.socialmedia.userservice.service;

import com.socialmedia.userservice.dto.CreateUserDto;
import com.socialmedia.userservice.dto.UpdateUserDto;
import com.socialmedia.userservice.dto.UserProfileDto;
import com.socialmedia.userservice.exception.UserAlreadyExistsException;
import com.socialmedia.userservice.exception.UserNotFoundException;
import com.socialmedia.userservice.mapper.UserMapper;
import com.socialmedia.userservice.mapper.UserNodeMapper;
import com.socialmedia.userservice.repository.neo4j.UserGraphRepository;
import com.socialmedia.userservice.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserGraphRepository userGraphRepository;
    private final UserMapper userMapper;
    private final UserNodeMapper userNodeMapper;

    public Long createUser(CreateUserDto dto) {
        if (userRepository.existsByUsername(dto.getUsername()))
            throw new UserAlreadyExistsException("Username already taken");

        if (userRepository.existsByEmail(dto.getEmail()))
            throw new UserAlreadyExistsException("Email already registered");

        var user = userMapper.toEntity(dto);
        var savedUser = userRepository.save(user);
        // TODO: Apply Outbox Pattern here
        var userNode = userNodeMapper.toEntity(savedUser);
        userGraphRepository.save(userNode);

        return savedUser.getId();
    }

    public UserProfileDto getUserProfile(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        var dto = userMapper.toUserProfileDto(user);

        dto.setFollowerCount(userGraphRepository.countFollowers(userId));
        dto.setFollowingCount(userGraphRepository.countFollowing(userId));

        return dto;
    }

    public void updateUserProfile(Long userId, UpdateUserDto dto) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (dto.getFirstName() != null && !dto.getFirstName().isBlank())
            user.setFirstName(dto.getFirstName());

        if (dto.getLastName() != null && !dto.getLastName().isBlank())
            user.setLastName(dto.getLastName());

        if (dto.getBio() != null)
            user.setBio(dto.getBio());

        if (dto.getProfilePictureUrl() != null)
            user.setProfilePictureUrl(dto.getProfilePictureUrl());

        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userGraphRepository.deleteUserAndRelationships(userId);
        userRepository.deleteById(user.getId());
    }
}
