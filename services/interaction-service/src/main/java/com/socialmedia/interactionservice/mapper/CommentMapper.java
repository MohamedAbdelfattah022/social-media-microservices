package com.socialmedia.interactionservice.mapper;

import com.socialmedia.interactionservice.dto.CommentDto;
import com.socialmedia.interactionservice.dto.CreateCommentDto;
import com.socialmedia.interactionservice.dto.CursorPageResponse;
import com.socialmedia.interactionservice.dto.projection.CommentProjection;
import com.socialmedia.interactionservice.entity.Comment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentMapper {

    public Comment toEntity(CreateCommentDto dto, Long postId, String userId) {
        return Comment.builder()
                .content(dto.getContent())
                .postId(postId)
                .userId(userId)
                .isEdited(false)
                .build();
    }

    public Comment toEntity(CreateCommentDto dto, Comment parentComment, String userId) {
        return Comment.builder()
                .content(dto.getContent())
                .postId(parentComment.getPostId())
                .parentComment(parentComment)
                .userId(userId)
                .isEdited(false)
                .build();
    }

    public CommentDto toDto(CommentProjection projection) {
        return CommentDto.builder()
                .id(projection.getId())
                .postId(projection.getPostId())
                .userId(projection.getUserId())
                .content(projection.getContent())
                .parentCommentId(projection.getParentCommentId())
                .isEdited(projection.getIsEdited())
                .createdAt(projection.getCreatedAt().toString())
                .updatedAt(projection.getUpdatedAt() != null ? projection.getUpdatedAt().toString() : null)
                .likeCount(projection.getLikeCount())
                .replyCount(projection.getReplyCount())
                .build();
    }

    public CursorPageResponse<CommentDto> toPaginationResponse(
            List<CommentDto> commentDtos, String nextCursor,
            boolean hasNext, Integer pageSize) {
        return CursorPageResponse.<CommentDto>builder()
                .data(commentDtos)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .pageSize(pageSize)
                .build();
    }

    public List<CommentDto> toCommentDtos(List<CommentProjection> projections) {
        return projections.stream()
                .map(this::toDto)
                .toList();
    }
}
