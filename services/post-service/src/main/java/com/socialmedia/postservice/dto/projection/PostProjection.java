package com.socialmedia.postservice.dto.projection;

import java.time.LocalDateTime;

public interface PostProjection {
    Long getId();

    String getUserId();

    String getUserName();

    String getFirstName();

    String getLastName();

    String getProfilePictureUrl();

    String getContent();

    String getPrivacy();

    String[] getMediaUrls();

    Boolean getIsEdited();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();
}
