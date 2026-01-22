package com.socialmedia.userservice.grpc;

import com.socialmedia.grpc.user.GetFollowingRequest;
import com.socialmedia.grpc.user.GetFollowingResponse;
import com.socialmedia.grpc.user.UserInfo;
import com.socialmedia.grpc.user.UserServiceGrpc;
import com.socialmedia.userservice.dto.UserProfileDto;
import com.socialmedia.userservice.service.SocialGraphService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {
    private final SocialGraphService socialGraphService;

    @Override
    public void getFollowing(GetFollowingRequest request, StreamObserver<GetFollowingResponse> responseObserver) {
        log.debug("gRPC getFollowing for userId: {}", request.getUserId());

        List<UserProfileDto> following = socialGraphService.getFollowing(request.getUserId());

        GetFollowingResponse.Builder responseBuilder = GetFollowingResponse.newBuilder();

        for (UserProfileDto user : following) {
            responseBuilder.addUsers(
                    UserInfo.newBuilder()
                            .setId(user.getId())
                            .setFirstName(user.getFirstName() != null ? user.getFirstName() : "")
                            .setLastName(user.getLastName() != null ? user.getLastName() : "")
                            .setUsername(user.getUsername() != null ? user.getUsername() : "")
                            .setProfilePictureUrl(user.getProfilePictureUrl() != null ? user.getProfilePictureUrl() : "")
                            .build()
            );
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
