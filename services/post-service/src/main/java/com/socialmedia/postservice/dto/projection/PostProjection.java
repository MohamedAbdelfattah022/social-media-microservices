package com.socialmedia.postservice.dto.projection;

import java.time.LocalDateTime;

public interface PostProjection {
    Long getId();

    String getUserId();

    String getContent();

    String getPrivacy();

    String[] getMediaUrls();

    Boolean getIsEdited();

    //    Long getLikeCount();
//    Long getCommentCount();
    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();
}
