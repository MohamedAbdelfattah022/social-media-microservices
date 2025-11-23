package com.socialmedia.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    private String firstName;
    private String lastName;
    private String bio;
    private String profilePictureUrl;
}