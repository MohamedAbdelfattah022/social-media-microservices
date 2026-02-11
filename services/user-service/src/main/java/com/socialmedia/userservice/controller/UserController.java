package com.socialmedia.userservice.controller;

import com.socialmedia.userservice.dto.UpdateUserDto;
import com.socialmedia.userservice.dto.UserProfileDto;
import com.socialmedia.userservice.service.SocialGraphService;
import com.socialmedia.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SocialGraphService socialGraphService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getUserProfile());
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<UserProfileDto>> getSuggestions(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(socialGraphService.getSuggestions(limit));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> updateUserProfile(@RequestBody @Valid UpdateUserDto dto) {
        userService.updateUserProfile(dto);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/follow/{followeeId}")
    public ResponseEntity<Void> followUser(@PathVariable String followeeId) {
        socialGraphService.followUser(followeeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/follow/{followeeId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable String followeeId) {
        socialGraphService.unfollowUser(followeeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserProfileDto>> getFollowers(@PathVariable String userId) {
        return ResponseEntity.ok(socialGraphService.getFollowers(userId));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserProfileDto>> getFollowing(@PathVariable String userId) {
        return ResponseEntity.ok(socialGraphService.getFollowing(userId));
    }

}
