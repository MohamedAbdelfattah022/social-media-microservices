package com.socialmedia.interactionservice.grpc;

import com.socialmedia.grpc.interaction.*;
import com.socialmedia.interactionservice.repository.CommentRepository;
import com.socialmedia.interactionservice.repository.LikeRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InteractionGrpcService extends InteractionServiceGrpc.InteractionServiceImplBase {

    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    @Override
    public void getPostInteractionCounts(
            GetPostInteractionCountsRequest request,
            StreamObserver<GetPostInteractionCountsResponse> responseObserver) {

        log.debug("gRPC getPostInteractionCounts for {} posts", request.getPostIdsCount());
        List<Long> postIds = request.getPostIdsList();

        GetPostInteractionCountsResponse.Builder responseBuilder =
                GetPostInteractionCountsResponse.newBuilder();

        for (Long postId : postIds) {
            long likeCount = likeRepository.countByPostId(postId);
            long commentCount = commentRepository.countByPostId(postId);

            PostInteractionCounts counts = PostInteractionCounts.newBuilder()
                    .setPostId(postId)
                    .setLikeCount(likeCount)
                    .setCommentCount(commentCount)
                    .build();

            responseBuilder.addCounts(counts);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
