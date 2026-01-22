package com.socialmedia.feedservice.service;

import com.socialmedia.feedservice.client.IFollowingProvider;
import com.socialmedia.feedservice.client.IPostsProvider;
import com.socialmedia.feedservice.dto.CursorPageResponse;
import com.socialmedia.feedservice.dto.FeedItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final IFollowingProvider followingProvider;
    private final IPostsProvider postsProvider;

    public CursorPageResponse<FeedItemDto> getFeed(String userId, String cursor, int pageSize) {
        log.debug("Getting feed for user: {}, cursor: {}, pageSize: {}", userId, cursor, pageSize);

        var followingIds = followingProvider.getFollowingUserIds(userId);

        if (followingIds.isEmpty()) {
            log.debug("User {} is not following anyone. Returning empty feed.", userId);
            return CursorPageResponse.<FeedItemDto>builder()
                    .data(Collections.emptyList())
                    .nextCursor(null)
                    .hasNext(false)
                    .pageSize(0)
                    .build();
        }

        var feedPage = postsProvider.getPostsByUserIds(followingIds, cursor, pageSize);
        log.debug("Fetched {} feed items for user: {}", feedPage.getData().size(), userId);

        return feedPage;
    }
}
