package com.example.cryptopayment.domain.repository;

import java.util.Optional;

public interface RefundIdempotencyStore {
    Optional<String> findRefundNo(String idempotencyKey);

    void save(String idempotencyKey, String refundNo);
}
