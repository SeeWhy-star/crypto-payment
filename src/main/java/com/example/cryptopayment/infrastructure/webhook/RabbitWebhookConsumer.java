package com.example.cryptopayment.infrastructure.webhook;

import com.example.cryptopayment.domain.model.WebhookEvent;
import com.example.cryptopayment.domain.repository.WebhookEventDeduplicationStore;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Profile("rabbitmq")
@EnableRetry
public class RabbitWebhookConsumer {
    private final WebhookDeliveryService deliveryService;
    private final WebhookEventDeduplicationStore deduplicationStore;
    private final RabbitTemplate rabbitTemplate;

    public RabbitWebhookConsumer(WebhookDeliveryService deliveryService,
                                 WebhookEventDeduplicationStore deduplicationStore,
                                 RabbitTemplate rabbitTemplate) {
        this.deliveryService = deliveryService;
        this.deduplicationStore = deduplicationStore;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitWebhookConfig.QUEUE)
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 500, multiplier = 2))
    public void consume(WebhookEvent event) {
        if (!deduplicationStore.markIfNew(event.eventId())) {
            return;
        }
        deliveryService.deliver(event);
    }

    @Recover
    public void recover(RuntimeException exception, WebhookEvent event) {
        rabbitTemplate.convertAndSend(RabbitWebhookConfig.DEAD_LETTER_EXCHANGE,
                RabbitWebhookConfig.DEAD_LETTER_ROUTING_KEY, event);
    }
}
