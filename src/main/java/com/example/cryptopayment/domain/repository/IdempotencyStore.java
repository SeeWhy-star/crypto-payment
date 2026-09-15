package com.example.cryptopayment.domain.repository;

import java.util.Optional;

public interface IdempotencyStore {
    Optional<String> findPaymentNo(String idempotencyKey);

    void save(String idempotencyKey, String paymentNo);
}
