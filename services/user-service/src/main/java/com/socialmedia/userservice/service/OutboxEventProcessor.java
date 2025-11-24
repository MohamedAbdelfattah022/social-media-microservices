package com.socialmedia.userservice.service;

import com.socialmedia.userservice.entity.neo4j.UserNode;
import com.socialmedia.userservice.entity.postgres.OutboxEvent;
import com.socialmedia.userservice.enums.OutboxEventStatus;
import com.socialmedia.userservice.enums.OutboxEventType;
import com.socialmedia.userservice.repository.neo4j.UserGraphRepository;
import com.socialmedia.userservice.repository.postgres.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventProcessor {
    private static final int MAX_RETRIES = 5;

    private final OutboxEventRepository outboxEventRepository;
    private final UserGraphRepository userGraphRepository;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000, initialDelay = 10000)
    @Transactional
    public void processOutboxEvents() {
        var pendingEvents = outboxEventRepository.findPendingEvents();
        if (pendingEvents.isEmpty()) return;

        log.info("Processing {} outbox events", pendingEvents.size());

        for (OutboxEvent event : pendingEvents)
            processEvent(event);
    }

    protected void processEvent(OutboxEvent event) {
        try {
            log.debug("Processing event: id={}, type={}", event.getId(), event.getEventType());

            switch (event.getEventType()) {
                case OutboxEventType.USER_CREATED -> handleCreatedEvent(event);
                case OutboxEventType.USER_DELETED -> handleDeletedEvent(event);
                default -> throw new IllegalArgumentException("Unknown event type: " + event.getEventType());
            }

            event.setStatus(OutboxEventStatus.PROCESSED);
            event.setProcessedAt(LocalDateTime.now());
            outboxEventRepository.save(event);

            log.info("Successfully processed event: id={}, type={}", event.getId(), event.getEventType());
        } catch (Exception e) {
            handleEventFailure(event, e);
        }
    }

    private void handleCreatedEvent(OutboxEvent event) {
        JsonNode payload = objectMapper.readTree(event.getPayload());

        UserNode userNode = UserNode.builder()
                .userId(payload.get("userId").asLong())
                .username(payload.get("username").asString())
                .createdAt(LocalDateTime.parse(payload.get("createdAt").asString()))
                .build();

        userGraphRepository.save(userNode);
    }

    private void handleDeletedEvent(OutboxEvent event) {
        Long userId = event.getAggregateId();
        userGraphRepository.deleteUserAndRelationships(userId);
    }

    private void handleEventFailure(OutboxEvent event, Exception e) {
        log.error("Failed to process event: id={}, type={}, attempt={}", event.getId(), event.getEventType(),
                event.getRetryCount() + 1, e);

        event.setRetryCount(event.getRetryCount() + 1);
        event.setErrorMessage(e.getMessage());

        if (event.getRetryCount() > MAX_RETRIES) {
            event.setStatus(OutboxEventStatus.FAILED);
            log.error("Event marked as FAILED after {} retries: id={}", event.getRetryCount(), event.getId());
        }

        outboxEventRepository.save(event);
    }
}
