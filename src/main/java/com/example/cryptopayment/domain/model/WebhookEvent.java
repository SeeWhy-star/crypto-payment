package com.example.cryptopayment.domain.model;

import java.time.Instant;

public record WebhookEvent(
        String eventId,
        String eventType,
        String paymentNo,
        String payload,
        Instant createdAt
) {
}
