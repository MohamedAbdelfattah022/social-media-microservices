package com.socialmedia.feedservice.security;

import org.springframework.security.oauth2.jwt.Jwt;

public record AuthenticatedUser(String id, String email) {
    public static AuthenticatedUser fromJwt(Jwt jwt) {
        return new AuthenticatedUser(
                jwt.getSubject(),
                jwt.getClaimAsString("email")
        );
    }
}