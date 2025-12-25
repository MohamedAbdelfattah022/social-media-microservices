package com.socialmedia.interactionservice.service;

import com.socialmedia.interactionservice.entity.Like;
import com.socialmedia.interactionservice.exception.ResourceNotFoundException;
import com.socialmedia.interactionservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostServiceClient postServiceClient;
    private final LikeRepository likeRepository;

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
    }

    @Transactional
    public void unlikeComment(Long commentId, String userId) {
        likeRepository.deleteByUserIdAndCommentId(userId, commentId);
        log.info("User {} unliked comment {}", userId, commentId);
    }
}
