package com.socialmedia.postservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(Object message) {
        rabbitTemplate.convertAndSend("commentFanoutExchange", "", message);
    }
}
