package com.socialmedia.postservice.service;

import com.socialmedia.postservice.dto.event.PostDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;

    public static final String POST_DELETED_EXCHANGE = "postDeletedExchange";

    public void sendMessage(Object message) {
        rabbitTemplate.convertAndSend("commentFanoutExchange", "", message);
    }

    public void sendPostDeletedMessage(PostDeletedEvent event) {
        rabbitTemplate.convertAndSend(POST_DELETED_EXCHANGE, "", event);
    }
}
