package com.socialmedia.postservice.mapper;

import com.socialmedia.postservice.consts.EventType;
import com.socialmedia.postservice.dto.event.Event;
import com.socialmedia.postservice.dto.event.PostDeletedEvent;
import com.socialmedia.postservice.entity.Post;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventFactory {

    public Event createEvent(Post post, String eventType) {
        return switch (eventType) {
            case EventType.POST_CREATED -> buildPostCreatedEvent(post);
            case EventType.POST_UPDATED -> buildPostUpdatedEvent(post);
            default -> throw new IllegalArgumentException("Unsupported event type: " + eventType);
        };
    }

    public PostDeletedEvent createPostDeletedEvent(Long postId, List<UUID> fileIds) {
        List<String> fileIdStrings = fileIds.stream()
                .map(UUID::toString)
                .toList();

        return PostDeletedEvent.builder()
                .postId(postId)
                .fileIds(fileIdStrings)
                .deletedAt(LocalDateTime.now())
                .build();
    }

    private Event buildPostCreatedEvent(Post post) {
        return Event.builder()
                .postId(post.getId())
                .userId(post.getUserId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .eventType(EventType.POST_CREATED)
                .build();
    }

    private Event buildPostUpdatedEvent(Post post) {
        return Event.builder()
                .postId(post.getId())
                .userId(post.getUserId())
                .content(post.getContent())
                .updatedAt(post.getUpdatedAt())
                .eventType(EventType.POST_UPDATED)
                .build();
    }
}
