package com.example.cryptopayment.infrastructure.webhook;

import com.example.cryptopayment.domain.model.WebhookEvent;

public interface WebhookPublisher {
    void publish(WebhookEvent event);
}
