package com.socialmedia.postservice.service;

import com.socialmedia.postservice.config.RabbitConfig;
import com.socialmedia.postservice.dto.event.NotificationEvent;
import com.socialmedia.postservice.dto.event.PostDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducer {
    public static final String POST_DELETED_EXCHANGE = "postDeletedExchange";
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(Object message) {
        rabbitTemplate.convertAndSend("commentFanoutExchange", "", message);
    }

    public void sendPostDeletedMessage(PostDeletedEvent event) {
        rabbitTemplate.convertAndSend(POST_DELETED_EXCHANGE, "", event);
    }

    public void publishNotificationEvent(NotificationEvent event, String routingKey) {
        log.debug("Publishing notification event: type={}, eventId={}", event.getEventType(), event.getEventId());

        rabbitTemplate.convertAndSend(
                RabbitConfig.NOTIFICATION_EXCHANGE,
                routingKey,
                event
        );
    }
}
