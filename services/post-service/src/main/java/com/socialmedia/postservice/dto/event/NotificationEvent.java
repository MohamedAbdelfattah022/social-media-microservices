package com.socialmedia.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String eventId;
    private String eventType;
    private String sourceService;
    private Instant timestamp;
    private String actorUserId;
    private String targetUserId;
    private String resourceType;
    private String resourceId;
    private Map<String, Object> metadata;
}
