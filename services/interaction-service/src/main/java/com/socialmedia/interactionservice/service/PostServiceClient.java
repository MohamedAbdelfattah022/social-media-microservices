package com.socialmedia.interactionservice.service;

import com.socialmedia.grpc.post.CheckPostExistsRequest;
import com.socialmedia.grpc.post.CheckPostExistsResponse;
import com.socialmedia.grpc.post.PostServiceGrpc;
import com.socialmedia.interactionservice.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceClient {

    private final PostServiceGrpc.PostServiceBlockingStub postServiceStub;

    public void validatePostOrThrow(Long postId) {
        log.debug("Validating post existence via gRPC: {}", postId);

        if (!postExists(postId)) {
            log.warn("Post not found: {}", postId);
            throw new PostNotFoundException(postId);
        }

        log.debug("Post validated successfully: {}", postId);
    }

    public boolean postExists(Long postId) {
        CheckPostExistsRequest request = CheckPostExistsRequest.newBuilder()
                .setPostId(postId)
                .build();

        CheckPostExistsResponse response = postServiceStub.checkPostExists(request);
        return response.getExists();
    }
}
