package com.socialmedia.userservice.service;

import com.socialmedia.userservice.dto.UserProfileDto;
import com.socialmedia.userservice.entity.neo4j.UserNode;
import com.socialmedia.userservice.exception.UserNotFoundException;
import com.socialmedia.userservice.mapper.UserMapper;
import com.socialmedia.userservice.repository.neo4j.UserGraphRepository;
import com.socialmedia.userservice.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialGraphService {
    private final UserGraphRepository userGraphRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void followUser(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId))
            throw new IllegalArgumentException("User cannot follow themselves");

        if (!userRepository.existsById(followerId) || !userRepository.existsById(followeeId))
            throw new UserNotFoundException("One or both users not found");

        if (userGraphRepository.isFollowing(followerId, followeeId))
            return;

        userGraphRepository.createFollowRelationship(followerId, followeeId);
    }

    public void unfollowUser(Long followerId, Long followeeId) {
        if (!userGraphRepository.isFollowing(followerId, followeeId))
            throw new IllegalStateException("Not following this user");

        userGraphRepository.deleteFollowRelationship(followerId, followeeId);
    }

    public List<UserProfileDto> getFollowers(Long userId) {
        List<UserNode> followerNodes = userGraphRepository.findFollowers(userId);

        List<Long> followerIds = followerNodes.stream()
                .map(UserNode::getUserId)
                .toList();

        return userRepository.findAllById(followerIds).stream()
                .map(userMapper::toUserProfileDto)
                .toList();
    }

    public List<UserProfileDto> getFollowing(Long userId) {
        List<UserNode> followingNodes = userGraphRepository.findFollowing(userId);

        List<Long> followingIds = followingNodes.stream()
                .map(UserNode::getUserId)
                .toList();

        return userRepository.findAllById(followingIds).stream()
                .map(userMapper::toUserProfileDto)
                .toList();
    }
}
