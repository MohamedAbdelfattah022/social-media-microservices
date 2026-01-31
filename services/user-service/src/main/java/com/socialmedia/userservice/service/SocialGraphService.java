package com.socialmedia.userservice.service;

import com.socialmedia.userservice.dto.UserProfileDto;
import com.socialmedia.userservice.dto.event.NotificationEvent;
import com.socialmedia.userservice.dto.event.NotificationEventType;
import com.socialmedia.userservice.dto.event.ResourceType;
import com.socialmedia.userservice.entity.neo4j.UserNode;
import com.socialmedia.userservice.entity.postgres.UserProfile;
import com.socialmedia.userservice.exception.UserNotFoundException;
import com.socialmedia.userservice.repository.neo4j.UserGraphRepository;
import com.socialmedia.userservice.repository.postgres.UserProfileRepository;
import com.socialmedia.userservice.security.KeycloakUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialGraphService {
    private final UserGraphRepository userGraphRepository;
    private final UserProfileRepository userProfileRepository;
    private final KeycloakUserContext userContext;
    private final UserService userService;
    private final NotificationEventPublisher notificationEventPublisher;


    public void followUser(String followeeId) {
        String followerId = userContext.getCurrentUserId();

        if (followerId.equals(followeeId))
            throw new IllegalArgumentException("User cannot follow themselves");

        if (!userProfileRepository.existsById(followeeId))
            throw new UserNotFoundException("User to follow not found");

        if (userGraphRepository.isFollowing(followerId, followeeId)) return;

        userGraphRepository.createFollowRelationship(followerId, followeeId);

        publishUserFollowedEvent(followerId, followeeId);
    }

    public void unfollowUser(String followeeId) {
        String followerId = userContext.getCurrentUserId();

        if (!userGraphRepository.isFollowing(followerId, followeeId))
            throw new IllegalStateException("Not following this user");

        userGraphRepository.deleteFollowRelationship(followerId, followeeId);
    }

    public List<UserProfileDto> getFollowers(String userId) {
        List<UserNode> followerNodes = userGraphRepository.findFollowers(userId);

        List<String> followerIds = followerNodes.stream()
                .map(UserNode::getUserId)
                .toList();

        return userProfileRepository.findAllById(followerIds).stream()
                .map(profile -> UserProfileDto.builder()
                        .id(profile.getId())
                        .firstName(profile.getFirstName())
                        .lastName(profile.getLastName())
                        .bio(profile.getBio())
                        .profilePictureUrl(profile.getProfilePictureUrl())
                        .build())
                .collect(Collectors.toList());
    }

    public List<UserProfileDto> getFollowing(String userId) {
        List<UserNode> followingNodes = userGraphRepository.findFollowing(userId);

        List<String> followingIds = followingNodes.stream()
                .map(UserNode::getUserId)
                .toList();

        return userProfileRepository.findAllById(followingIds).stream()
                .map(profile -> UserProfileDto.builder()
                        .id(profile.getId())
                        .firstName(profile.getFirstName())
                        .lastName(profile.getLastName())
                        .bio(profile.getBio())
                        .profilePictureUrl(profile.getProfilePictureUrl())
                        .build())
                .collect(Collectors.toList());
    }

    public boolean isFollowing(String followeeId) {
        String followerId = userContext.getCurrentUserId();
        return userGraphRepository.isFollowing(followerId, followeeId);
    }

    private void publishUserFollowedEvent(String followerId, String followeeId) {
        UserProfile followerProfile = userProfileRepository.findById(followerId).orElse(null);

        Map<String, Object> metadata = new HashMap<>();
        if (followerProfile != null) {
            metadata.put("actorUsername", followerProfile.getFirstName() + " " + followerProfile.getLastName());
            metadata.put("actorDisplayName", followerProfile.getFirstName() + " " + followerProfile.getLastName());
            metadata.put("actorProfilePicture", followerProfile.getProfilePictureUrl());
        }

        NotificationEvent event = NotificationEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(NotificationEventType.USER_FOLLOWED)
                .sourceService("user-service")
                .timestamp(Instant.now())
                .actorUserId(followerId)
                .targetUserId(followeeId)
                .resourceType(ResourceType.USER)
                .resourceId(followeeId)
                .metadata(metadata)
                .build();

        notificationEventPublisher.publish(event, "user.followed");
    }
}
