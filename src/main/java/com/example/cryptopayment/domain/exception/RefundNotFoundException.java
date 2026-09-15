package com.example.cryptopayment.domain.exception;

public class RefundNotFoundException extends RuntimeException {
    public RefundNotFoundException(String refundNo) {
        super("Refund not found: " + refundNo);
    }
}
