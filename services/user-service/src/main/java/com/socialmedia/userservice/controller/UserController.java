package com.socialmedia.userservice.controller;

import com.socialmedia.userservice.dto.CreateUserDto;
import com.socialmedia.userservice.dto.UpdateUserDto;
import com.socialmedia.userservice.dto.UserProfileDto;
import com.socialmedia.userservice.service.SocialGraphService;
import com.socialmedia.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SocialGraphService socialGraphService;

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody @Valid CreateUserDto dto) {
        var id = userService.createUser(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody @Valid UpdateUserDto dto) {
        userService.updateUserProfile(userId, dto);
        return ResponseEntity.noContent().build();
    }

    // TODO: (Copilot Review) The API design exposes followerId as a path parameter,
    // allowing any user to follow on behalf of another user. This is a security
    // vulnerability. The followerId should be derived from the authenticated user's
    // session/token, not from the request path.
    @PostMapping("/{followerId}/follow/{followeeId}")
    public ResponseEntity<Void> followUser(
            @PathVariable Long followerId,
            @PathVariable Long followeeId) {
        socialGraphService.followUser(followerId, followeeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // TODO: (Copilot Review) The API design exposes followerId as a path parameter,
    // allowing any user to unfollow on behalf of another user. This is a security
    // vulnerability. The followerId should be derived from the authenticated user's
    // session/token, not from the request path.
    @DeleteMapping("/{followerId}/follow/{followeeId}")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable Long followerId,
            @PathVariable Long followeeId) {
        socialGraphService.unfollowUser(followerId, followeeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserProfileDto>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(socialGraphService.getFollowers(userId));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserProfileDto>> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(socialGraphService.getFollowing(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
