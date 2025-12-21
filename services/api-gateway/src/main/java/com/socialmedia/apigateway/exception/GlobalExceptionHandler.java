package com.socialmedia.apigateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Error occurred: ", ex);

        HttpStatus status = resolveStatus(ex);
        String message = resolveMessage(ex);

        return writeJsonError(exchange, status, message);
    }

    private HttpStatus resolveStatus(Throwable ex) {
        if (ex instanceof NotFoundException)
            return HttpStatus.SERVICE_UNAVAILABLE;

        if (ex instanceof ResponseStatusException responseStatusException)
            return (HttpStatus) responseStatusException.getStatusCode();

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String resolveMessage(Throwable ex) {
        if (ex instanceof NotFoundException)
            return "Service is currently unavailable";

        if (ex instanceof ResponseStatusException responseStatusException)
            return responseStatusException.getReason();

        return "An unexpected error occurred";
    }

    private Mono<Void> writeJsonError(ServerWebExchange exchange, HttpStatus status, String message) {
        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String json = createJson(status, message);

        DataBuffer buffer = response.bufferFactory()
                .wrap(json.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    private String createJson(HttpStatus status, String message) {
        return """
                {
                  "error": "%s",
                  "message": "%s",
                  "status": %d
                }
                """.formatted(status.getReasonPhrase(), message, status.value());
    }
}
