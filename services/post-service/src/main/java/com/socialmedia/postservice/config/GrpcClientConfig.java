package com.socialmedia.postservice.config;

import com.socialmedia.grpc.user.UserServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientConfig {

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub userServiceStub(GrpcChannelFactory channel) {
        return UserServiceGrpc.newBlockingStub(
                channel.createChannel("user-service")
        );
    }
}
