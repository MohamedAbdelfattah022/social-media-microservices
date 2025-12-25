package com.socialmedia.interactionservice.repository;

import com.socialmedia.interactionservice.dto.projection.CommentProjection;
import com.socialmedia.interactionservice.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = """
            SELECT c.id,
                   c.post_id,
                   c.user_id,
                   c.parent_comment_id,
                   c.content,
                   c.is_edited,
                   c.created_at,
                   c.updated_at,
                   (SELECT COUNT(1) FROM Comments cc WHERE cc.parent_comment_id = c.id) AS reply_count,
                   (SELECT COUNT(1) FROM likes cl WHERE cl.comment_id = c.id)           AS like_count
            FROM Comments c
            WHERE c.post_id = :postId
              AND c.parent_comment_id IS NULL
              AND ((CAST(:cursor AS TIMESTAMP) IS NULL OR c.created_at < CAST(:cursor AS TIMESTAMP)) OR
                   (c.created_at = CAST(:cursor AS TIMESTAMP) AND c.id < :lastId))
            ORDER BY c.created_at DESC, c.id DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<CommentProjection> findByPostIdWithCursor(
            @Param("postId") Long postId,
            @Param("cursor") LocalDateTime cursor,
            @Param("lastId") Long lastId,
            @Param("limit") Integer limit);

    @Query(value = """
            SELECT c.id,
                   c.post_id,
                   c.user_id,
                   c.parent_comment_id,
                   c.content,
                   c.is_edited,
                   c.created_at,
                   c.updated_at,
                   (SELECT COUNT(1) FROM Comments cc WHERE cc.parent_comment_id = c.id) AS reply_count,
                   (SELECT COUNT(1) FROM likes cl WHERE cl.comment_id = c.id)           AS like_count
            FROM Comments c
            WHERE c.parent_comment_id = :parentCommentId
              AND ((CAST(:cursor AS TIMESTAMP) IS NULL OR c.created_at < CAST(:cursor AS TIMESTAMP)) OR
                   (c.created_at = CAST(:cursor AS TIMESTAMP) AND c.id < :lastId))
            ORDER BY c.created_at DESC, c.id DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<CommentProjection> findRepliesByParentCommentIdWithCursor(
            @Param("parentCommentId") Long parentCommentId,
            @Param("cursor") LocalDateTime cursor,
            @Param("lastId") Long lastId,
            @Param("limit") Integer limit);
}