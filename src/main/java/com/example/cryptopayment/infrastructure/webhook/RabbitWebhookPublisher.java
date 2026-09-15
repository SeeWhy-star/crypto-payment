package com.example.cryptopayment.infrastructure.webhook;

import com.example.cryptopayment.domain.model.WebhookEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("rabbitmq")
public class RabbitWebhookPublisher implements WebhookPublisher {
    public static final String EXCHANGE = "crypto-payment.webhooks";
    public static final String ROUTING_KEY = "webhook.delivery";

    private final RabbitTemplate rabbitTemplate;

    public RabbitWebhookPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(WebhookEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
    }
}
