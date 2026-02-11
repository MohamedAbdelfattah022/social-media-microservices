package com.socialmedia.interactionservice.service;

import com.socialmedia.grpc.user.UserInfo;
import com.socialmedia.interactionservice.dto.CommentDto;
import com.socialmedia.interactionservice.dto.CreateCommentDto;
import com.socialmedia.interactionservice.dto.CursorPageResponse;
import com.socialmedia.interactionservice.dto.event.NotificationEvent;
import com.socialmedia.interactionservice.dto.event.NotificationEventType;
import com.socialmedia.interactionservice.dto.event.ResourceType;
import com.socialmedia.interactionservice.dto.projection.CommentProjection;
import com.socialmedia.interactionservice.entity.Comment;
import com.socialmedia.interactionservice.exception.ResourceNotFoundException;
import com.socialmedia.interactionservice.exception.ResourceOwnershipException;
import com.socialmedia.interactionservice.grpc.UserServiceClient;
import com.socialmedia.interactionservice.mapper.CommentMapper;
import com.socialmedia.interactionservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostServiceClient postServiceClient;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CursorPaginationService cursorPaginationService;
    private final NotificationEventPublisher notificationEventPublisher;
    private final UserServiceClient userServiceClient;

    public Long addCommentToPost(CreateCommentDto dto, Long postId, String userId) {
        postServiceClient.validatePostOrThrow(postId);

        Comment comment = commentMapper.toEntity(dto, postId, userId);
        Comment savedComment = commentRepository.save(comment);

        publishPostCommentedEvent(savedComment, postId, userId);

        return savedComment.getId();
    }

    public Long replyToComment(CreateCommentDto dto, Long commentId, String userId) {
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        Comment replyComment = commentMapper.toEntity(dto, parentComment, userId);

        Comment savedReply = commentRepository.save(replyComment);

        publishCommentRepliedEvent(savedReply, parentComment, userId);

        return savedReply.getId();
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
        enrichWithUserInfo(commentDtos);

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
        enrichWithUserInfo(commentDtos);

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

    private void enrichWithUserInfo(List<CommentDto> commentDtos) {
        Set<String> userIds = commentDtos.stream()
                .map(CommentDto::getUserId)
                .collect(Collectors.toSet());

        Map<String, UserInfo> userInfoMap = userServiceClient.getUserInfoBatch(userIds);

        for (CommentDto dto : commentDtos) {
            UserInfo userInfo = userInfoMap.get(dto.getUserId());
            if (userInfo != null) {
                dto.setFirstname(userInfo.getFirstName());
                dto.setLastname(userInfo.getLastName());
            }
        }
    }

    private void verifyOwnership(String ownerId, String currentUserId) {
        if (!ownerId.equals(currentUserId)) {
            throw new ResourceOwnershipException("You do not have permission to perform this action");
        }
    }

    private void publishPostCommentedEvent(Comment comment, Long postId, String actorUserId) {
        postServiceClient.getPostOwnerId(postId).ifPresent(postOwnerId -> {
            if (postOwnerId.equals(actorUserId)) {
                return;
            }

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("postId", postId.toString());
            metadata.put("commentId", comment.getId().toString());
            String preview = comment.getContent().length() > 50
                    ? comment.getContent().substring(0, 50) + "..."
                    : comment.getContent();
            metadata.put("commentPreview", preview);

            NotificationEvent event = NotificationEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(NotificationEventType.POST_COMMENTED)
                    .sourceService("interaction-service")
                    .timestamp(Instant.now())
                    .actorUserId(actorUserId)
                    .targetUserId(postOwnerId)
                    .resourceType(ResourceType.COMMENT)
                    .resourceId(comment.getId().toString())
                    .metadata(metadata)
                    .build();

            notificationEventPublisher.publish(event, "post.commented");
        });
    }

    private void publishCommentRepliedEvent(Comment reply, Comment parentComment, String actorUserId) {
        String parentCommentOwnerId = parentComment.getUserId();
        if (parentCommentOwnerId.equals(actorUserId)) {
            return;
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("postId", parentComment.getPostId().toString());
        metadata.put("parentCommentId", parentComment.getId().toString());
        metadata.put("replyCommentId", reply.getId().toString());
        String preview = reply.getContent().length() > 50
                ? reply.getContent().substring(0, 50) + "..."
                : reply.getContent();
        metadata.put("replyPreview", preview);

        NotificationEvent event = NotificationEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(NotificationEventType.COMMENT_REPLIED)
                .sourceService("interaction-service")
                .timestamp(Instant.now())
                .actorUserId(actorUserId)
                .targetUserId(parentCommentOwnerId)
                .resourceType(ResourceType.COMMENT)
                .resourceId(reply.getId().toString())
                .metadata(metadata)
                .build();

        notificationEventPublisher.publish(event, "comment.replied");
    }
}
