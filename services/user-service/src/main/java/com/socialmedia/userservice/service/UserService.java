package com.socialmedia.userservice.service;

import com.socialmedia.userservice.dto.SignupRequest;
import com.socialmedia.userservice.dto.UpdateUserDto;
import com.socialmedia.userservice.dto.UserProfileDto;
import com.socialmedia.userservice.entity.postgres.UserProfile;
import com.socialmedia.userservice.exception.UserNotFoundException;
import com.socialmedia.userservice.repository.neo4j.UserGraphRepository;
import com.socialmedia.userservice.repository.postgres.UserProfileRepository;
import com.socialmedia.userservice.security.KeycloakUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserProfileRepository userProfileRepository;
    private final UserGraphRepository userGraphRepository;
    private final KeycloakUserContext userContext;

    public UserProfileDto getUserProfile() {
        String userId = userContext.getCurrentUserId();

        UserProfileDto profile = userProfileRepository.findProfileById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        profile.setFollowerCount(userGraphRepository.countFollowers(userId));
        profile.setFollowingCount(userGraphRepository.countFollowing(userId));

        return profile;
    }

    public UserProfileDto getUserProfile(String userId) {
        UserProfileDto profile = userProfileRepository.findProfileById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        profile.setFollowerCount(userGraphRepository.countFollowers(userId));
        profile.setFollowingCount(userGraphRepository.countFollowing(userId));

        return profile;
    }

    @Transactional
    public void updateUserProfile(UpdateUserDto dto) {
        String userId = userContext.getCurrentUserId();

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (StringUtils.hasText(dto.getFirstName()))
            profile.setFirstName(dto.getFirstName());
        if (StringUtils.hasText(dto.getLastName()))
            profile.setLastName(dto.getLastName());

        if (dto.getBio() != null)
            profile.setBio(dto.getBio());
        if (dto.getProfilePictureUrl() != null)
            profile.setProfilePictureUrl(dto.getProfilePictureUrl());

        userProfileRepository.save(profile);
    }

    public void createProfile(String userId, SignupRequest request) {
        UserProfile profile = UserProfile.builder()
                .id(userId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        userProfileRepository.save(profile);

    }

    @Transactional(readOnly = true)
    public List<UserProfileDto> searchUsers(String query, int limit) {
        String currentUserId = userContext.getCurrentUserId();

        if (query == null || query.trim().isEmpty())
            throw new IllegalArgumentException("Search query cannot be empty");

        if (limit <= 0 || limit > 50) limit = 10;

        List<UserProfileDto> users = userProfileRepository
                .searchUsersByUsername(query.trim(), currentUserId, limit);

        users.forEach(user -> {
            user.setFollowerCount(userGraphRepository.countFollowers(user.getId()));
            user.setFollowingCount(userGraphRepository.countFollowing(user.getId()));
        });

        return users;
    }
}
