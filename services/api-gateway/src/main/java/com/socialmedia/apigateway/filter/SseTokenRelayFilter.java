package com.socialmedia.apigateway.filter;

import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class SseTokenRelayFilter implements WebFilter, Ordered {

    private static final String ACCESS_TOKEN_PARAM = "access_token";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (request.getHeaders().getFirst(AUTHORIZATION_HEADER) == null) {
            String accessToken = request.getQueryParams().getFirst(ACCESS_TOKEN_PARAM);

            if (accessToken != null && !accessToken.isEmpty()) {
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
