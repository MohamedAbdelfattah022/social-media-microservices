package com.socialmedia.interactionservice.service;

import com.socialmedia.interactionservice.dto.CommentDto;
import com.socialmedia.interactionservice.dto.CreateCommentDto;
import com.socialmedia.interactionservice.dto.CursorPageResponse;
import com.socialmedia.interactionservice.dto.projection.CommentProjection;
import com.socialmedia.interactionservice.entity.Comment;
import com.socialmedia.interactionservice.exception.ResourceNotFoundException;
import com.socialmedia.interactionservice.exception.ResourceOwnershipException;
import com.socialmedia.interactionservice.mapper.CommentMapper;
import com.socialmedia.interactionservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostServiceClient postServiceClient;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CursorPaginationService cursorPaginationService;

    public Long addCommentToPost(CreateCommentDto dto, Long postId, String userId) {
        postServiceClient.validatePostOrThrow(postId);

        Comment comment = commentMapper.toEntity(dto, postId, userId);
        return commentRepository.save(comment).getId();
    }

    public Long replyToComment(CreateCommentDto dto, Long commentId, String userId) {
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        Comment replyComment = commentMapper.toEntity(dto, parentComment, userId);

        return commentRepository.save(replyComment).getId();
    }

    public CursorPageResponse<CommentDto> getCommentsForPost(Long postId, String cursor, Integer pageSize) {
        LocalDateTime cursorTime = null;
        Long lastId = null;

        if (cursor != null && !cursor.isEmpty()) {
            String[] parts = cursorPaginationService.decodeCursor(cursor);
            cursorTime = cursorPaginationService.parseCursorTimestamp(parts);
            lastId = cursorPaginationService.parseCursorId(parts);
        }

        List<CommentProjection> projections = commentRepository.findByPostIdWithCursor(
                postId,
                cursorTime,
                lastId,
                Math.max(pageSize + 1, 1));

        boolean hasNext = projections.size() > pageSize;
        if (hasNext)
            projections = projections.subList(0, pageSize);

        List<CommentDto> commentDtos = commentMapper.toCommentDtos(projections);

        String nextCursor = null;
        if (hasNext && !commentDtos.isEmpty()) {
            CommentDto lastComment = commentDtos.getLast();
            nextCursor = cursorPaginationService.encodeCursor(lastComment.getCreatedAt(), lastComment.getId());
        }

        return commentMapper.toPaginationResponse(commentDtos, nextCursor, hasNext, pageSize);
    }

    public CursorPageResponse<CommentDto> getRepliesForComment(Long commentId, String cursor, Integer pageSize) {
        if (!commentRepository.existsById(commentId)) {
            throw new ResourceNotFoundException("Comment not found with id: " + commentId);
        }

        LocalDateTime cursorTime = null;
        Long lastId = null;

        if (cursor != null && !cursor.isEmpty()) {
            String[] parts = cursorPaginationService.decodeCursor(cursor);
            cursorTime = cursorPaginationService.parseCursorTimestamp(parts);
            lastId = cursorPaginationService.parseCursorId(parts);
        }

        List<CommentProjection> projections = commentRepository.findRepliesByParentCommentIdWithCursor(
                commentId,
                cursorTime,
                lastId,
                Math.max(pageSize + 1, 1));

        boolean hasNext = projections.size() > pageSize;
        if (hasNext)
            projections = projections.subList(0, pageSize);

        List<CommentDto> commentDtos = commentMapper.toCommentDtos(projections);

        String nextCursor = null;
        if (hasNext && !commentDtos.isEmpty()) {
            CommentDto lastComment = commentDtos.getLast();
            nextCursor = cursorPaginationService.encodeCursor(lastComment.getCreatedAt(), lastComment.getId());
        }

        return commentMapper.toPaginationResponse(commentDtos, nextCursor, hasNext, pageSize);
    }

    public void updateComment(CreateCommentDto commentDto, Long commentId, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        verifyOwnership(comment.getUserId(), userId);

        comment.setContent(commentDto.getContent());
        comment.setIsEdited(true);

        commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        verifyOwnership(comment.getUserId(), userId);

        commentRepository.deleteById(commentId);
    }

    private void verifyOwnership(String ownerId, String currentUserId) {
        if (!ownerId.equals(currentUserId)) {
            throw new ResourceOwnershipException("You do not have permission to perform this action");
        }
    }
}
