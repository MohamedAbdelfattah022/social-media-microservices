package com.socialmedia.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String bio;
    private String profilePictureUrl;
    private Long followerCount;
    private Long followingCount;

    public UserProfileDto(
            String id, String firstName, String lastName,
            String username, String email, String bio,
            String profilePictureUrl
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.profilePictureUrl = profilePictureUrl;
    }
}
