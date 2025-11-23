package com.socialmedia.userservice.mapper;

import com.socialmedia.userservice.entity.neo4j.UserNode;
import com.socialmedia.userservice.entity.postgres.User;
import org.springframework.stereotype.Service;

@Service
public class UserNodeMapper {
    public UserNode toEntity(User user) {
        return UserNode.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
