package com.example.cryptopayment.infrastructure.webhook;

import com.example.cryptopayment.domain.model.WebhookEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Profile("!rabbitmq")
public class InMemoryWebhookPublisher implements WebhookPublisher {
    private final Queue<WebhookEvent> events = new ConcurrentLinkedQueue<>();

    @Override
    public void publish(WebhookEvent event) {
        events.add(event);
    }

    public int size() {
        return events.size();
    }
}
