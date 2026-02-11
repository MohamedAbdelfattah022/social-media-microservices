package com.socialmedia.interactionservice.grpc;

import com.socialmedia.grpc.user.UserInfo;
import com.socialmedia.grpc.user.UserInfoRequest;
import com.socialmedia.grpc.user.UserServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceClient {
    private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    public Map<String, UserInfo> getUserInfoBatch(Set<String> userIds) {
        Map<String, UserInfo> result = new HashMap<>();

        for (String userId : userIds) {
            try {
                UserInfo userInfo = userServiceStub.getUserInfo(
                        UserInfoRequest.newBuilder().setUserId(userId).build()
                );
                result.put(userId, userInfo);
            } catch (Exception e) {
                log.warn("Failed to fetch user info for userId: {}", userId, e);
            }
        }

        return result;
    }
}
