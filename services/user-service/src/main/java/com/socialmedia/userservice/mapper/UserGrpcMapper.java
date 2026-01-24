package com.socialmedia.userservice.mapper;

import com.socialmedia.grpc.user.UserInfo;
import com.socialmedia.userservice.dto.UserProfileDto;
import org.springframework.stereotype.Service;

@Service
public class UserGrpcMapper {
    public UserInfo toUserInfo(UserProfileDto user) {
        return UserInfo.newBuilder()
                .setId(user.getId())
                .setFirstName(valueOrEmpty(user.getFirstName()))
                .setLastName(valueOrEmpty(user.getLastName()))
                .setUsername(valueOrEmpty(user.getUsername()))
                .setProfilePictureUrl(valueOrEmpty(user.getProfilePictureUrl()))
                .build();
    }

    private String valueOrEmpty(String value) {
        return value != null ? value : "";
    }
}
