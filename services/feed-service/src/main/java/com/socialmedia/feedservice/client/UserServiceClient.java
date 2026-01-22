package com.socialmedia.feedservice.client;

import com.socialmedia.grpc.user.GetFollowingRequest;
import com.socialmedia.grpc.user.GetFollowingResponse;
import com.socialmedia.grpc.user.UserInfo;
import com.socialmedia.grpc.user.UserServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceClient implements IFollowingProvider {

    private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    @Override
    public List<String> getFollowingUserIds(String userId) {
        log.debug("Fetching following list for user: {}", userId);

        GetFollowingRequest request = GetFollowingRequest.newBuilder()
                .setUserId(userId)
                .build();

        GetFollowingResponse response = userServiceStub.getFollowing(request);

        List<String> userIds = response.getUsersList().stream()
                .map(UserInfo::getId)
                .toList();

        log.debug("Found {} users in following list", userIds.size());
        return userIds;
    }
}