package com.socialmedia.feedservice.client;

import com.socialmedia.feedservice.dto.CursorPageResponse;
import com.socialmedia.feedservice.dto.FeedItemDto;
import com.socialmedia.feedservice.mapper.FeedItemMapper;
import com.socialmedia.grpc.post.GetPostsByUserIdsRequest;
import com.socialmedia.grpc.post.GetPostsByUserIdsResponse;
import com.socialmedia.grpc.post.PostServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceClient implements IPostsProvider {

    private final PostServiceGrpc.PostServiceBlockingStub postServiceStub;
    private final FeedItemMapper feedItemMapper;

    @Override
    public CursorPageResponse<FeedItemDto> getPostsByUserIds(List<String> userIds, String cursor, int pageSize) {
        log.debug("Fetching posts for {} users, pageSize: {}", userIds.size(), pageSize);

        GetPostsByUserIdsRequest.Builder requestBuilder = GetPostsByUserIdsRequest.newBuilder()
                .addAllUserIds(userIds)
                .setPageSize(pageSize);

        if (cursor != null && !cursor.isBlank()) {
            requestBuilder.setCursor(cursor);
        }

        GetPostsByUserIdsResponse response = postServiceStub.getPostsByUserIds(requestBuilder.build());

        List<FeedItemDto> items = feedItemMapper.toFeedItems(response.getPostsList());

        return CursorPageResponse.<FeedItemDto>builder()
                .data(items)
                .nextCursor(response.getNextCursor())
                .hasNext(response.getHasMore())
                .pageSize(pageSize)
                .build();
    }
}
