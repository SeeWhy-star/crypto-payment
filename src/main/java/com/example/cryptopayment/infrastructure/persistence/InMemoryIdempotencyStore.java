package com.example.cryptopayment.infrastructure.persistence;

import com.example.cryptopayment.domain.repository.IdempotencyStore;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("!redis")
public class InMemoryIdempotencyStore implements IdempotencyStore {
    private final ConcurrentHashMap<String, String> keys = new ConcurrentHashMap<>();

    @Override
    public Optional<String> findPaymentNo(String idempotencyKey) {
        return Optional.ofNullable(keys.get(idempotencyKey));
    }

    @Override
    public void save(String idempotencyKey, String paymentNo) {
        keys.putIfAbsent(idempotencyKey, paymentNo);
    }
}
