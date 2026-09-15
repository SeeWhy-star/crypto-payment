package com.example.cryptopayment.infrastructure.webhook;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("rabbitmq")
public class RabbitWebhookConfig {
    public static final String QUEUE = "crypto-payment.webhook-delivery";

    @Bean
    TopicExchange webhookExchange() {
        return new TopicExchange(RabbitWebhookPublisher.EXCHANGE);
    }

    @Bean
    Queue webhookQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    Binding webhookBinding(Queue webhookQueue, TopicExchange webhookExchange) {
        return BindingBuilder.bind(webhookQueue)
                .to(webhookExchange)
                .with(RabbitWebhookPublisher.ROUTING_KEY);
    }

    @Bean
    Jackson2JsonMessageConverter webhookMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
