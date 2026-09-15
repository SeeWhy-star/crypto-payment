package com.example.cryptopayment.domain.model;

import com.example.cryptopayment.domain.enums.RefundStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record Refund(
        String refundNo,
        String paymentNo,
        BigDecimal amount,
        RefundStatus status,
        Instant createdAt
) {
}
