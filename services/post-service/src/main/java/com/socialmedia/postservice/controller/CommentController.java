package com.socialmedia.postservice.controller;

import com.socialmedia.postservice.dto.CommentDto;
import com.socialmedia.postservice.dto.CreateCommentDto;
import com.socialmedia.postservice.dto.CursorPageResponse;
import com.socialmedia.postservice.dto.UpdateCommentDto;
import com.socialmedia.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("")
    public ResponseEntity<Void> createComment(@PathVariable Long postId, @RequestBody CreateCommentDto commentDto) {
        var id = commentService.createComment(postId, commentDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("")
    public ResponseEntity<CursorPageResponse<CommentDto>> getComments(
            @PathVariable Long postId,
            @RequestParam(required = false) String cursor,
            @RequestParam Integer pageSize
    ) {
        if (pageSize <= 0) pageSize = 10;

        var comments = commentService.getComments(postId, cursor, pageSize);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<CursorPageResponse<CommentDto>> getCommentReplies(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestParam(required = false) String cursor,
            @RequestParam Integer pageSize
    ) {
        if (pageSize <= 0) pageSize = 10;

        var replies = commentService.getCommentReplies(postId, commentId, cursor, pageSize);
        return ResponseEntity.ok(replies);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @RequestBody UpdateCommentDto dto,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        commentService.updateComment(dto, postId, commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(postId, commentId);
        return ResponseEntity.noContent().build();
    }
}
