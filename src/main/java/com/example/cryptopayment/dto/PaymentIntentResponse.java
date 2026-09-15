package com.example.cryptopayment.dto;

import com.example.cryptopayment.domain.enums.PaymentIntentStatus;
import com.example.cryptopayment.domain.model.PaymentIntent;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentIntentResponse(
        String paymentNo,
        BigDecimal amount,
        String currency,
        PaymentIntentStatus status,
        Instant createdAt
) {
    public static PaymentIntentResponse from(PaymentIntent paymentIntent) {
        return new PaymentIntentResponse(
                paymentIntent.paymentNo(),
                paymentIntent.amount(),
                paymentIntent.currency(),
                paymentIntent.status(),
                paymentIntent.createdAt()
        );
    }
}
