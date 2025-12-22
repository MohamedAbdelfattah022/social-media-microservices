package com.socialmedia.userservice.controller;

import com.socialmedia.userservice.dto.SignupRequest;
import com.socialmedia.userservice.service.KeycloakAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final KeycloakAdminService keycloakAdminService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request) {
        String response = keycloakAdminService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
