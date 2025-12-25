package com.socialmedia.interactionservice.config;

import com.socialmedia.grpc.post.PostServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientConfig {

    @Bean
    public PostServiceGrpc.PostServiceBlockingStub postServiceStub(GrpcChannelFactory channels) {
        return PostServiceGrpc.newBlockingStub(
            channels.createChannel("post-service")
        );
    }
}
