package com.socialmedia.userservice.service;

import com.socialmedia.userservice.dto.CreateUserDto;
import com.socialmedia.userservice.dto.UpdateUserDto;
import com.socialmedia.userservice.dto.UserProfileDto;
import com.socialmedia.userservice.entity.postgres.OutboxEvent;
import com.socialmedia.userservice.entity.postgres.User;
import com.socialmedia.userservice.enums.OutboxEventAggregateType;
import com.socialmedia.userservice.enums.OutboxEventStatus;
import com.socialmedia.userservice.enums.OutboxEventType;
import com.socialmedia.userservice.exception.UserAlreadyExistsException;
import com.socialmedia.userservice.exception.UserNotFoundException;
import com.socialmedia.userservice.mapper.UserMapper;
import com.socialmedia.userservice.repository.neo4j.UserGraphRepository;
import com.socialmedia.userservice.repository.postgres.OutboxEventRepository;
import com.socialmedia.userservice.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserGraphRepository userGraphRepository;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public Long createUser(CreateUserDto dto) {
        if (userRepository.existsByUsername(dto.getUsername()))
            throw new UserAlreadyExistsException("Username already taken");

        if (userRepository.existsByEmail(dto.getEmail()))
            throw new UserAlreadyExistsException("Email already registered");

        var user = userMapper.toEntity(dto);
        var savedUser = userRepository.save(user);

        createOutboxEvent(
                savedUser.getId(),
                OutboxEventType.USER_CREATED,
                createUserCreatedPayload(savedUser));

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

    @Transactional
    public void deleteUser(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        createOutboxEvent(
                user.getId(),
                OutboxEventType.USER_DELETED,
                "{}");

        userRepository.deleteById(user.getId());
    }

    private void createOutboxEvent(Long id, String eventType, String payload) {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateType(OutboxEventAggregateType.USER)
                .aggregateId(id)
                .eventType(eventType)
                .payload(payload)
                .status(OutboxEventStatus.PENDING)
                .retryCount(0)
                .build();

        outboxEventRepository.save(outboxEvent);
    }

    private String createUserCreatedPayload(User user) {
        try {
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("userId", user.getId());
            payload.put("username", user.getUsername());
            payload.put("createdAt", user.getCreatedAt().toString());

            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create outbox event payload", e);
        }
    }
}
