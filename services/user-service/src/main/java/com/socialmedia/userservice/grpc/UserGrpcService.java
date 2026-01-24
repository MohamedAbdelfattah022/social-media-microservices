package com.socialmedia.userservice.grpc;

import com.socialmedia.grpc.user.*;
import com.socialmedia.userservice.dto.UserProfileDto;
import com.socialmedia.userservice.mapper.UserGrpcMapper;
import com.socialmedia.userservice.service.SocialGraphService;
import com.socialmedia.userservice.service.UserService;
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
    private final UserService userService;
    private final UserGrpcMapper userGrpcMapper;

    @Override
    public void getFollowing(GetFollowingRequest request, StreamObserver<GetFollowingResponse> responseObserver) {
        log.debug("gRPC getFollowing for userId: {}", request.getUserId());

        List<UserProfileDto> following = socialGraphService.getFollowing(request.getUserId());

        GetFollowingResponse.Builder responseBuilder = GetFollowingResponse.newBuilder();

        for (UserProfileDto user : following) {
            responseBuilder.addUsers(
                    userGrpcMapper.toUserInfo(user)
            );
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getUserInfo(UserInfoRequest request, StreamObserver<UserInfo> responseObserver) {

        log.debug("gRPC getUserInfo for userId: {}", request.getUserId());

        UserProfileDto userProfile = userService.getUserProfile(request.getUserId());
        UserInfo userInfo = userGrpcMapper.toUserInfo(userProfile);

        responseObserver.onNext(userInfo);
        responseObserver.onCompleted();
    }

}
