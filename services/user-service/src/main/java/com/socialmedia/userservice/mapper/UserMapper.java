package com.socialmedia.userservice.mapper;

import com.socialmedia.userservice.dto.CreateUserDto;
import com.socialmedia.userservice.dto.UserProfileDto;
import com.socialmedia.userservice.entity.postgres.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public User toEntity(CreateUserDto dto) {
        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .bio(dto.getBio())
                .profilePictureUrl(dto.getProfilePictureUrl())
                .build();
    }

    public UserProfileDto toUserProfileDto(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .bio(user.getBio())
                .profilePictureUrl(user.getProfilePictureUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
