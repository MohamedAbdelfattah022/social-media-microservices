package com.socialmedia.interactionservice.repository;

import com.socialmedia.interactionservice.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    long countByPostId(Long postId);

    long countByCommentId(Long commentId);

    Optional<Like> findByUserIdAndPostId(String userId, Long postId);

    Optional<Like> findByUserIdAndCommentId(String userId, Long commentId);

    boolean existsByUserIdAndPostId(String userId, Long postId);

    boolean existsByUserIdAndCommentId(String userId, Long commentId);

    void deleteByUserIdAndPostId(String userId, Long postId);

    void deleteByUserIdAndCommentId(String userId, Long commentId);
}
