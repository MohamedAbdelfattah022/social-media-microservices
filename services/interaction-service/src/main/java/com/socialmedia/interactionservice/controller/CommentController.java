package com.socialmedia.interactionservice.controller;

import com.socialmedia.interactionservice.dto.CommentDto;
import com.socialmedia.interactionservice.dto.CreateCommentDto;
import com.socialmedia.interactionservice.dto.CursorPageResponse;
import com.socialmedia.interactionservice.security.AuthenticatedUser;
import com.socialmedia.interactionservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<Long> addCommentToPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateCommentDto commentDto) {
        var user = AuthenticatedUser.fromJwt(jwt);
        Long id = commentService.addCommentToPost(commentDto, postId, user.id());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PostMapping("/{commentId}/reply")
    public ResponseEntity<Long> replyToComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateCommentDto commentDto) {
        var user = AuthenticatedUser.fromJwt(jwt);
        Long id = commentService.replyToComment(commentDto, commentId, user.id());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<CursorPageResponse<CommentDto>> getPostComments(
            @PathVariable Long postId,
            @RequestParam(required = false) String cursor,
            @RequestParam Integer pageSize) {
        var comments = commentService.getCommentsForPost(postId, cursor, pageSize);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<CursorPageResponse<CommentDto>> getCommentReplies(
            @PathVariable Long commentId,
            @RequestParam(required = false) String cursor,
            @RequestParam Integer pageSize) {
        var replies = commentService.getRepliesForComment(commentId, cursor, pageSize);
        return ResponseEntity.ok(replies);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody CreateCommentDto commentDto,
            @AuthenticationPrincipal Jwt jwt) {
        var user = AuthenticatedUser.fromJwt(jwt);
        commentService.updateComment(commentDto, commentId, user.id());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Jwt jwt) {
        var user = AuthenticatedUser.fromJwt(jwt);
        commentService.deleteComment(commentId, user.id());
        return ResponseEntity.noContent().build();
    }
}
