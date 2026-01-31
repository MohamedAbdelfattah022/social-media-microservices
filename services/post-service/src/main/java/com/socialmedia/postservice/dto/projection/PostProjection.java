package com.socialmedia.postservice.dto.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface PostProjection {
    Long getId();

    String getUserId();

    String getUserName();

    String getFirstName();

    String getLastName();

    String getProfilePictureUrl();

    String getContent();

    String getPrivacy();

    UUID[] getFileIds();

    Boolean getIsEdited();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();
}
