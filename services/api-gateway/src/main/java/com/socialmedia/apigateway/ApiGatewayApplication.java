package com.socialmedia.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/api/users/**", "/api/auth/**")
                        .uri("lb://USER-SERVICE")
                )
                .route(p -> p
                        .path("/api/posts/**")
                        .uri("lb://POST-SERVICE")
                )
                .route(p -> p
                        .path("/api/likes/**", "/api/comments/**")
                        .uri("lb://INTERACTION-SERVICE")
                )
                .route(p -> p
                        .path("/api/feed/**")
                        .uri("lb://FEED-SERVICE")
                )
                .route(p -> p
                        .path("/api/files/**")
                        .uri("lb://MINIO-SERVICE")
                )
                .build();
    }
}
