package com.payetonkawa.client.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nom de la file d'attente
    public static final String CLIENT_CHANGE_QUEUE = "client-change-queue";

    // Définir le bean de la file d'attente
    @Bean
    public Queue clientChangeQueue() {
       // Création d'une file d'attente durable, qui survit au redémarrage de RabbitMQ

        return new Queue(CLIENT_CHANGE_QUEUE, true, false, false);
    }
}
