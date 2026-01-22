package com.socialmedia.feedservice.client;

import com.socialmedia.feedservice.dto.CursorPageResponse;
import com.socialmedia.feedservice.dto.FeedItemDto;

import java.util.List;

public interface IPostsProvider {
    CursorPageResponse<FeedItemDto> getPostsByUserIds(List<String> userIds, String cursor, int pageSize);
}
