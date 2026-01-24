package com.socialmedia.postservice.dto;

import java.io.Serializable;

public record UserDto(
        String id,
        String username,
        String firstName,
        String lastName,
        String profilePictureUrl
) implements Serializable {
}