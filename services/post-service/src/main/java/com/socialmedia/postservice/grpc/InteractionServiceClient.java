package com.socialmedia.postservice.grpc;

import com.socialmedia.grpc.interaction.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InteractionServiceClient {

    private final InteractionServiceGrpc.InteractionServiceBlockingStub interactionServiceStub;

    public Map<Long, PostInteractionCounts> getInteractionCounts(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyMap();
        }

        log.debug("Fetching interaction counts for {} posts via gRPC", postIds.size());

        try {
            GetPostInteractionCountsRequest request = GetPostInteractionCountsRequest.newBuilder()
                    .addAllPostIds(postIds)
                    .build();

            GetPostInteractionCountsResponse response = interactionServiceStub.getPostInteractionCounts(request);

            return response.getCountsList().stream()
                    .collect(Collectors.toMap(
                            PostInteractionCounts::getPostId,
                            counts -> counts));
        } catch (Exception e) {
            log.error("Failed to fetch interaction counts via gRPC: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }
}
