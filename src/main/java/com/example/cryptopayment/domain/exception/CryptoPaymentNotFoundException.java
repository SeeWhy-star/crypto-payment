package com.example.cryptopayment.domain.exception;

public class CryptoPaymentNotFoundException extends RuntimeException {
    public CryptoPaymentNotFoundException(String paymentNo) {
        super("Crypto payment not found for payment intent: " + paymentNo);
    }
}
