package com.example.cryptopayment.infrastructure.webhook;

import com.example.cryptopayment.domain.model.WebhookEvent;

public interface WebhookDeliveryService {
    void deliver(WebhookEvent event);
}
