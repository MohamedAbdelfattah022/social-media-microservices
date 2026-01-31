package com.socialmedia.interactionservice.dto.event;

public final class NotificationEventType {
    private NotificationEventType() {}

    // User events
    public static final String USER_FOLLOWED = "USER_FOLLOWED";

    // Post events
    public static final String POST_CREATED = "POST_CREATED";
    public static final String POST_LIKED = "POST_LIKED";
    public static final String POST_COMMENTED = "POST_COMMENTED";

    // Comment events
    public static final String COMMENT_REPLIED = "COMMENT_REPLIED";
    public static final String COMMENT_LIKED = "COMMENT_LIKED";
}
