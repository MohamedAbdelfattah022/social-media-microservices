package com.socialmedia.postservice.grpc;


import com.socialmedia.grpc.user.UserInfo;
import com.socialmedia.grpc.user.UserInfoRequest;
import com.socialmedia.grpc.user.UserServiceGrpc;
import com.socialmedia.postservice.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceClient {
    private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    @Cacheable(value = "users", key = "#userId")
    public UserDto getUserInfo(String userId) {
        log.debug("Fetching user info for userId: {}", userId);

        UserInfo userInfo = userServiceStub.getUserInfo(
                UserInfoRequest.newBuilder().setUserId(userId).build()
        );

        return new UserDto(
                userInfo.getId(),
                userInfo.getUsername(),
                userInfo.getFirstName(),
                userInfo.getLastName(),
                userInfo.getProfilePictureUrl()
        );
    }
}
