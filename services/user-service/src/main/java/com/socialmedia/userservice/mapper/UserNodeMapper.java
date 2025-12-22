package com.socialmedia.userservice.mapper;

import com.socialmedia.userservice.entity.neo4j.UserNode;
import com.socialmedia.userservice.entity.postgres.UserProfile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserNodeMapper {
    public UserNode toEntity(UserProfile profile, String username) {
        return UserNode.builder()
                .userId(profile.getId())
                .username(username)
                .createdAt(profile.getCreatedAt() != null ? profile.getCreatedAt() : LocalDateTime.now())
                .build();
    }
}
