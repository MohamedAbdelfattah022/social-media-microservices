package com.socialmedia.feedservice.mapper;

import com.socialmedia.feedservice.dto.FeedItemDto;
import com.socialmedia.grpc.post.PostInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeedItemMapper {

    public FeedItemDto toFeedItem(PostInfo post) {
        return new FeedItemDto(
                post.getId(),
                post.getUserId(),
                post.getContent(),
                post.getPrivacy(),
                post.getMediaUrlsList(),
                post.getIsEdited(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    public List<FeedItemDto> toFeedItems(List<PostInfo> posts) {
        return posts.stream()
                .map(this::toFeedItem)
                .toList();
    }
}
