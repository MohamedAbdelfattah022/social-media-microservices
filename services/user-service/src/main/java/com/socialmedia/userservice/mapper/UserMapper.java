package com.socialmedia.userservice.mapper;

import com.socialmedia.userservice.dto.UserProfileDto;
import com.socialmedia.userservice.entity.postgres.UserProfile;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public UserProfileDto toProfileDto(
            UserProfile profile,
            String userId,
            String username,
            String email,
            Long followerCount,
            Long followingCount) {
        return UserProfileDto.builder()
                .id(userId)
                .username(username)
                .email(email)
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .bio(profile.getBio())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .followerCount(followerCount)
                .followingCount(followingCount)
                .build();
    }

}
