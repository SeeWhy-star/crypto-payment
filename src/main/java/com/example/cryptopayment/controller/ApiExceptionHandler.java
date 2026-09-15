package com.example.cryptopayment.controller;

import com.example.cryptopayment.domain.exception.PaymentIntentNotFoundException;
import com.example.cryptopayment.domain.exception.CryptoPaymentNotFoundException;
import com.example.cryptopayment.domain.exception.InvalidRefundException;
import com.example.cryptopayment.domain.exception.RefundNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(PaymentIntentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(PaymentIntentNotFoundException exception) {
        return Map.of("error", "PAYMENT_INTENT_NOT_FOUND", "message", exception.getMessage());
    }

    @ExceptionHandler(CryptoPaymentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleCryptoPaymentNotFound(CryptoPaymentNotFoundException exception) {
        return Map.of("error", "CRYPTO_PAYMENT_NOT_FOUND", "message", exception.getMessage());
    }

    @ExceptionHandler(InvalidRefundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Map<String, String> handleInvalidRefund(InvalidRefundException exception) {
        return Map.of("error", "INVALID_REFUND", "message", exception.getMessage());
    }

    @ExceptionHandler(RefundNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleRefundNotFound(RefundNotFoundException exception) {
        return Map.of("error", "REFUND_NOT_FOUND", "message", exception.getMessage());
    }
}
