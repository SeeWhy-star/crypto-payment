package com.example.cryptopayment.domain.model;

import com.example.cryptopayment.domain.enums.PaymentIntentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentIntent(
        String paymentNo,
        BigDecimal amount,
        String currency,
        PaymentIntentStatus status,
        Instant createdAt
) {
}
