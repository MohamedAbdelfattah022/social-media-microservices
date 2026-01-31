package com.socialmedia.postservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String NOTIFICATION_EXCHANGE = "notification.events";

    @Bean
    public Queue commentNotificationQueue() {
        return new Queue("commentNotificationQueue", true);
    }

    @Bean
    public Queue commentFeedQueue() {
        return new Queue("commentFeedQueue", true);
    }

    @Bean
    public Queue postDeletedQueue() {
        return new Queue("postDeletedQueue", true);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("commentFanoutExchange");
    }

    @Bean
    public FanoutExchange postDeletedExchange() {
        return new FanoutExchange("postDeletedExchange");
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }

    @Bean
    public Binding notificationBinding(Queue commentNotificationQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(commentNotificationQueue).to(fanoutExchange);
    }

    @Bean
    public Binding feedBinding(Queue commentFeedQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(commentFeedQueue).to(fanoutExchange);
    }

    @Bean
    public Binding postDeletedBinding(Queue postDeletedQueue, FanoutExchange postDeletedExchange) {
        return BindingBuilder.bind(postDeletedQueue).to(postDeletedExchange);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
