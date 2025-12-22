package com.socialmedia.userservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class KeycloakUserContext {

    public String getCurrentUserId() {
        Jwt jwt = getJwt();
        return jwt.getSubject();
    }

    public String getCurrentUsername() {
        Jwt jwt = getJwt();
        return jwt.getClaimAsString("preferred_username");
    }

    public String getCurrentEmail() {
        Jwt jwt = getJwt();
        return jwt.getClaimAsString("email");
    }

    public String getCurrentFirstName() {
        Jwt jwt = getJwt();
        return jwt.getClaimAsString("given_name");
    }

    public String getCurrentLastName() {
        Jwt jwt = getJwt();
        return jwt.getClaimAsString("family_name");
    }

    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && auth instanceof JwtAuthenticationToken;
    }

    private Jwt getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken();
        }
        throw new IllegalStateException("No authenticated user found in security context");
    }
}
