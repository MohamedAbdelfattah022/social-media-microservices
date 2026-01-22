package com.socialmedia.feedservice.dto;

import java.util.List;

public record FeedItemDto(
        Long id,
        String userId,
        String content,
        String privacy,
        List<String> mediaUrls,
        boolean isEdited,
        Long likeCount,
        Long commentCount,
        String createdAt,
        String updatedAt
) {
}