package com.example.cryptopayment.infrastructure.persistence;

import com.example.cryptopayment.domain.repository.RefundIdempotencyStore;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("!redis")
public class InMemoryRefundIdempotencyStore implements RefundIdempotencyStore {
    private final ConcurrentHashMap<String, String> keys = new ConcurrentHashMap<>();

    @Override
    public Optional<String> findRefundNo(String idempotencyKey) {
        return Optional.ofNullable(keys.get(idempotencyKey));
    }

    @Override
    public void save(String idempotencyKey, String refundNo) {
        keys.putIfAbsent(idempotencyKey, refundNo);
    }
}
