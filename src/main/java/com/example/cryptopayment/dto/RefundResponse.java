package com.example.cryptopayment.dto;

import com.example.cryptopayment.domain.enums.RefundStatus;
import com.example.cryptopayment.domain.model.Refund;

import java.math.BigDecimal;
import java.time.Instant;

public record RefundResponse(String refundNo, String paymentNo, BigDecimal amount,
                             RefundStatus status, Instant createdAt) {
    public static RefundResponse from(Refund refund) {
        return new RefundResponse(refund.refundNo(), refund.paymentNo(), refund.amount(),
                refund.status(), refund.createdAt());
    }
}
