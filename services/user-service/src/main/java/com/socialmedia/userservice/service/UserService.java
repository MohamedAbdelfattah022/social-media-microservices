package com.socialmedia.userservice.service;

import com.socialmedia.userservice.client.MinioFeignClient;
import com.socialmedia.userservice.dto.*;
import com.socialmedia.userservice.entity.postgres.UserProfile;
import com.socialmedia.userservice.exception.UserNotFoundException;
import com.socialmedia.userservice.repository.neo4j.UserGraphRepository;
import com.socialmedia.userservice.repository.postgres.UserProfileRepository;
import com.socialmedia.userservice.security.KeycloakUserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserProfileRepository userProfileRepository;
    private final UserGraphRepository userGraphRepository;
    private final KeycloakUserContext userContext;
    private final MinioFeignClient minioFeignClient;

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

    @Transactional
    public UploadProfilePictureResponse uploadProfilePicture(MultipartFile file) {
        String userId = userContext.getCurrentUserId();

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String oldPictureUrl = profile.getProfilePictureUrl();
        if (StringUtils.hasText(oldPictureUrl)) {
            try {
                String oldFileId = extractFileIdFromUrl(oldPictureUrl);
                if (oldFileId != null) {
                    minioFeignClient.deleteFile(oldFileId);
                    log.info("Deleted old profile picture fileId={} for userId={}", oldFileId, userId);
                }
            } catch (Exception e) {
                log.warn("Could not delete old profile picture for userId={}: {}", userId, e.getMessage());
            }
        }

        FileUploadResponse uploadResponse = minioFeignClient.uploadFile(file);
        String fileId = uploadResponse.getId();

        String publicUrl = minioFeignClient.getPublicUrl(fileId);

        profile.setProfilePictureUrl(publicUrl);
        userProfileRepository.save(profile);

        log.info("Profile picture updated for userId={}, fileId={}", userId, fileId);
        return new UploadProfilePictureResponse(publicUrl);
    }

    @Transactional
    public void deleteProfilePicture() {
        String userId = userContext.getCurrentUserId();

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String pictureUrl = profile.getProfilePictureUrl();
        if (!StringUtils.hasText(pictureUrl)) {
            return;
        }

        try {
            String fileId = extractFileIdFromUrl(pictureUrl);
            if (fileId != null) {
                minioFeignClient.deleteFile(fileId);
                log.info("Deleted profile picture fileId={} for userId={}", fileId, userId);
            }
        } catch (Exception e) {
            log.warn("Could not delete profile picture from storage for userId={}: {}", userId, e.getMessage());
        }

        profile.setProfilePictureUrl(null);
        userProfileRepository.save(profile);
    }

    private String extractFileIdFromUrl(String url) {
        if (url == null) return null;
        Matcher matcher = Pattern
                .compile("([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})")
                .matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }
}
