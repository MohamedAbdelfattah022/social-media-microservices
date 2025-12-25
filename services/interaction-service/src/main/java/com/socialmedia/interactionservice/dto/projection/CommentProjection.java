package com.socialmedia.interactionservice.dto.projection;

import java.time.LocalDateTime;

public interface CommentProjection {
    Long getId();

    Long getPostId();

    String getUserId();

    Long getParentCommentId();

    String getContent();

    Boolean getIsEdited();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    Long getReplyCount();

    Long getLikeCount();
}
