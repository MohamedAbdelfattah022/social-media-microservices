package com.socialmedia.interactionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class InteractionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InteractionServiceApplication.class, args);
    }

}
