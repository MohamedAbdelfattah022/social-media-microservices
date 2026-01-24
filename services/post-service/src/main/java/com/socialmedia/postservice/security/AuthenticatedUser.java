package com.socialmedia.postservice.security;

import org.springframework.security.oauth2.jwt.Jwt;

public record AuthenticatedUser(
        String id,
        String username,
        String email,
        String firstName,
        String lastName,
        String profilePictureUrl
) {
    public static AuthenticatedUser fromJwt(Jwt jwt) {
        return new AuthenticatedUser(
                jwt.getSubject(),
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("email"),
                null,
                null,
                null
        );
    }
}