package com.socialmedia.postservice.repository;

import com.socialmedia.postservice.dto.projection.PostProjection;
import com.socialmedia.postservice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = """
                SELECT
                    p.id,
                    p.user_id,
                    p.content,
                    p.privacy,
                    p.is_edited,
                    p.created_at,
                    p.updated_at,
                    (SELECT COALESCE(array_agg(media_url), '{}')
                     FROM post_media_urls
                     WHERE post_id = p.id) AS media_urls
                    -- (SELECT COUNT(*) FROM likes WHERE post_id = p.id) AS like_count,
                    -- (SELECT COUNT(*) FROM comments WHERE post_id = p.id) AS comment_count
                FROM posts p
                WHERE p.id = :id
                AND p.privacy = 'PUBLIC'
            """, nativeQuery = true)
    PostProjection findByIdAndCountLikesAndComments(@Param("id") Long id);

    @Query(value = """
                SELECT
                    p.id,
                    p.user_id,
                    p.content,
                    p.privacy,
                    p.is_edited,
                    p.created_at,
                    p.updated_at,
                    (SELECT COALESCE(array_agg(media_url), '{}')
                     FROM post_media_urls
                     WHERE post_id = p.id) AS media_urls
                    -- (SELECT COUNT(*) FROM likes WHERE post_id = p.id) AS like_count,
                    -- (SELECT COUNT(*) FROM comments WHERE post_id = p.id) AS comment_count
                FROM posts p
                WHERE p.user_id = :userId
                AND p.privacy = 'PUBLIC'
                AND (CAST(:cursor AS TIMESTAMP) IS NULL OR p.created_at < CAST(:cursor AS TIMESTAMP) OR (p.created_at = CAST(:cursor AS TIMESTAMP) AND p.id < :lastId))
                ORDER BY p.created_at DESC, p.id DESC
                LIMIT :limit
            """, nativeQuery = true)
    List<PostProjection> findByUserIdWithCursor(
            @Param("userId") String userId,
            @Param("cursor") LocalDateTime cursor,
            @Param("lastId") Long lastId,
            @Param("limit") Integer limit);
}