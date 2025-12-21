package com.socialmedia.postservice.service;

import com.socialmedia.postservice.dto.CommentDto;
import com.socialmedia.postservice.dto.CreateCommentDto;
import com.socialmedia.postservice.dto.CursorPageResponse;
import com.socialmedia.postservice.dto.UpdateCommentDto;
import com.socialmedia.postservice.repository.CommentRepository;
import com.socialmedia.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public Long createComment(Long postId, CreateCommentDto comment) {

        return null;
    }

    public CursorPageResponse<CommentDto> getComments(Long postId, String cursor, Integer pageSize) {
        return null;
    }

    public CursorPageResponse<CommentDto> getCommentReplies(Long postId, Long commentId, String cursor, Integer pageSize) {
        return null;
    }

    public void updateComment(UpdateCommentDto dto, Long postId, Long commentId) {
    }

    public void deleteComment(Long postId, Long commentId) {
    }
}
