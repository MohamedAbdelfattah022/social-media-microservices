package com.socialmedia.postservice.grpc;

import com.socialmedia.grpc.post.*;
import com.socialmedia.postservice.dto.CursorPageResponse;
import com.socialmedia.postservice.dto.PostDto;
import com.socialmedia.postservice.repository.PostRepository;
import com.socialmedia.postservice.service.PostService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostGrpcService extends PostServiceGrpc.PostServiceImplBase {

    private final PostRepository postRepository;
    private final PostService postService;

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

    @Override
    public void getPostsByUserIds(GetPostsByUserIdsRequest request, StreamObserver<GetPostsByUserIdsResponse> responseObserver) {
        log.debug("gRPC getPostsByUserIds for {} users", request.getUserIdsCount());
        List<String> userIds = request.getUserIdsList();
        int pageSize = request.getPageSize() > 0 ? request.getPageSize() : 10;
        String cursor = request.getCursor();

        CursorPageResponse<PostDto> postsPage = postService.getPostsByUserIds(userIds, cursor, pageSize);

        GetPostsByUserIdsResponse.Builder responseBuilder = GetPostsByUserIdsResponse.newBuilder();
        for (PostDto post : postsPage.getData()) {
            PostInfo.Builder postBuilder = PostInfo.newBuilder()
                    .setId(post.getId())
                    .setUserId(post.getUserId())
                    .setUsername(post.getUserName())
                    .setFirstName(post.getFirstName() != null ? post.getFirstName() : "")
                    .setLastName(post.getLastName() != null ? post.getLastName() : "")
                    .setProfilePictureUrl(post.getProfilePictureUrl() != null ? post.getProfilePictureUrl() : "")
                    .setContent(post.getContent() != null ? post.getContent() : "")
                    .setPrivacy(post.getPrivacy() != null ? post.getPrivacy().name() : "PUBLIC")
                    .setIsEdited(post.isEdited())
                    .setLikeCount(post.getLikeCount() != null ? post.getLikeCount() : 0L)
                    .setCommentCount(post.getCommentCount() != null ? post.getCommentCount() : 0L)
                    .setCreatedAt(post.getCreatedAt() != null ? post.getCreatedAt() : "")
                    .setUpdatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt() : "");

            if (post.getMediaUrls() != null)
                postBuilder.addAllMediaUrls(post.getMediaUrls());

            responseBuilder.addPosts(postBuilder.build());
        }

        responseBuilder.setNextCursor(postsPage.getNextCursor() != null ? postsPage.getNextCursor() : "");
        responseBuilder.setHasMore(postsPage.getHasNext());

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getPostOwner(GetPostOwnerRequest request, StreamObserver<GetPostOwnerResponse> responseObserver) {
        long postId = request.getPostId();
        log.debug("Getting post owner for postId: {}", postId);

        var post = postRepository.findById(postId);

        GetPostOwnerResponse.Builder responseBuilder = GetPostOwnerResponse.newBuilder();
        if (post.isPresent()) {
            responseBuilder.setUserId(post.get().getUserId());
            responseBuilder.setFound(true);
        } else {
            responseBuilder.setUserId("");
            responseBuilder.setFound(false);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
