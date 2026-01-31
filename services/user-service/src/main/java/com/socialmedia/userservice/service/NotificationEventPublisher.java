package com.socialmedia.userservice.service;

import com.socialmedia.userservice.config.NotificationRabbitConfig;
import com.socialmedia.userservice.dto.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(NotificationEvent event, String routingKey) {
        log.debug("Publishing notification event: type={}, eventId={}", event.getEventType(), event.getEventId());

        rabbitTemplate.convertAndSend(
                NotificationRabbitConfig.NOTIFICATION_EXCHANGE,
                routingKey,
                event
        );
    }
}
