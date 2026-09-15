package com.example.cryptopayment.domain.exception;

public class PaymentIntentNotFoundException extends RuntimeException {

    public PaymentIntentNotFoundException(String paymentNo) {
        super("Payment intent not found: " + paymentNo);
    }
}
