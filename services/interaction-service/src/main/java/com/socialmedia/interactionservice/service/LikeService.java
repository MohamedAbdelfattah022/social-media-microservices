package com.socialmedia.interactionservice.service;

import com.socialmedia.interactionservice.dto.event.NotificationEvent;
import com.socialmedia.interactionservice.dto.event.NotificationEventType;
import com.socialmedia.interactionservice.dto.event.ResourceType;
import com.socialmedia.interactionservice.entity.Comment;
import com.socialmedia.interactionservice.entity.Like;
import com.socialmedia.interactionservice.exception.ResourceNotFoundException;
import com.socialmedia.interactionservice.repository.CommentRepository;
import com.socialmedia.interactionservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostServiceClient postServiceClient;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final NotificationEventPublisher notificationEventPublisher;

    public Long getLikesCountForPost(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    @Transactional
    public void likePost(Long postId, String userId) {
        postServiceClient.validatePostOrThrow(postId);

        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
            log.info("User {} already liked post {}", userId, postId);
            return;
        }

        Like like = new Like();
        like.setUserId(userId);
        like.setPostId(postId);
        like.setCreatedAt(Instant.now());

        likeRepository.save(like);
        log.info("User {} liked post {}", userId, postId);

        publishPostLikedEvent(postId, userId);
    }

    @Transactional
    public void unlikePost(Long postId, String userId) {
        postServiceClient.validatePostOrThrow(postId);

        likeRepository.deleteByUserIdAndPostId(userId, postId);
        log.info("User {} unliked post {}", userId, postId);
    }

    public Long getLikesCountForComment(Long commentId) {
        return likeRepository.countByCommentId(commentId);
    }

    @Transactional
    public void likeComment(Long commentId, String userId) {

        if (likeRepository.existsByUserIdAndCommentId(userId, commentId)) {
            log.warn("Comment not found with id: {} for User {}", commentId, userId);
            throw new ResourceNotFoundException("Comment not found");
        }

        Like like = new Like();
        like.setUserId(userId);
        like.setCommentId(commentId);
        like.setCreatedAt(Instant.now());

        likeRepository.save(like);
        log.info("User {} liked comment {}", userId, commentId);

        publishCommentLikedEvent(commentId, userId);
    }

    @Transactional
    public void unlikeComment(Long commentId, String userId) {
        likeRepository.deleteByUserIdAndCommentId(userId, commentId);
        log.info("User {} unliked comment {}", userId, commentId);
    }

    private void publishPostLikedEvent(Long postId, String actorUserId) {
        postServiceClient.getPostOwnerId(postId).ifPresent(postOwnerId -> {
            if (postOwnerId.equals(actorUserId)) {
                return;
            }

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("postId", postId.toString());

            NotificationEvent event = NotificationEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(NotificationEventType.POST_LIKED)
                    .sourceService("interaction-service")
                    .timestamp(Instant.now())
                    .actorUserId(actorUserId)
                    .targetUserId(postOwnerId)
                    .resourceType(ResourceType.POST)
                    .resourceId(postId.toString())
                    .metadata(metadata)
                    .build();

            notificationEventPublisher.publish(event, "post.liked");
        });
    }

    private void publishCommentLikedEvent(Long commentId, String actorUserId) {
        commentRepository.findById(commentId).ifPresent(comment -> {
            String commentOwnerId = comment.getUserId();
            if (commentOwnerId.equals(actorUserId)) {
                return;
            }

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("commentId", commentId.toString());
            metadata.put("postId", comment.getPostId().toString());
            String preview = comment.getContent().length() > 50
                    ? comment.getContent().substring(0, 50) + "..."
                    : comment.getContent();
            metadata.put("commentPreview", preview);

            NotificationEvent event = NotificationEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(NotificationEventType.COMMENT_LIKED)
                    .sourceService("interaction-service")
                    .timestamp(Instant.now())
                    .actorUserId(actorUserId)
                    .targetUserId(commentOwnerId)
                    .resourceType(ResourceType.COMMENT)
                    .resourceId(commentId.toString())
                    .metadata(metadata)
                    .build();

            notificationEventPublisher.publish(event, "comment.liked");
        });
    }
}
