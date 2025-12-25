package com.socialmedia.postservice.grpc;

import com.socialmedia.grpc.post.CheckPostExistsRequest;
import com.socialmedia.grpc.post.CheckPostExistsResponse;
import com.socialmedia.grpc.post.PostServiceGrpc;
import com.socialmedia.postservice.repository.PostRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostGrpcService extends PostServiceGrpc.PostServiceImplBase {

    private final PostRepository postRepository;

    @Override
    public void checkPostExists(
            CheckPostExistsRequest request,
            StreamObserver<CheckPostExistsResponse> responseObserver) {

        long postId = request.getPostId();
        log.debug("Checking post existence for postId: {}", postId);

        boolean exists = postRepository.existsById(postId);

        CheckPostExistsResponse response = CheckPostExistsResponse.newBuilder()
                .setExists(exists)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
