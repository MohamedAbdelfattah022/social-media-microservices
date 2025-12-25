package com.socialmedia.interactionservice.controller;

import com.socialmedia.interactionservice.security.AuthenticatedUser;
import com.socialmedia.interactionservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/post/{postId}")
    public ResponseEntity<Long> getPostLikesCount(@PathVariable Long postId) {
        Long likesCount = likeService.getLikesCountForPost(postId);
        return ResponseEntity.ok(likesCount);
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<Void> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var user = AuthenticatedUser.fromJwt(jwt);
        likeService.likePost(postId, user.id());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var user = AuthenticatedUser.fromJwt(jwt);
        likeService.unlikePost(postId, user.id());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<Long> getCommentLikesCount(@PathVariable Long commentId) {
        Long likesCount = likeService.getLikesCountForComment(commentId);
        return ResponseEntity.ok(likesCount);
    }

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<Void> likeComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var user = AuthenticatedUser.fromJwt(jwt);
        likeService.likeComment(commentId, user.id());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> unlikeComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var user = AuthenticatedUser.fromJwt(jwt);
        likeService.unlikeComment(commentId, user.id());

        return ResponseEntity.noContent().build();
    }
}
