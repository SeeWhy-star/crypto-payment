package com.example.cryptopayment.domain.repository;

public interface IdempotencyLock {
    void execute(String key, Runnable action);
}
