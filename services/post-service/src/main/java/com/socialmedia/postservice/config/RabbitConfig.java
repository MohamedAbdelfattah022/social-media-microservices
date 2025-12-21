package com.socialmedia.postservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public Queue commentNotificationQueue() {
        return new Queue("commentNotificationQueue", true);
    }

    @Bean
    public Queue commentFeedQueue() {
        return new Queue("commentFeedQueue", true);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("commentFanoutExchange");
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
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, JacksonJsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
