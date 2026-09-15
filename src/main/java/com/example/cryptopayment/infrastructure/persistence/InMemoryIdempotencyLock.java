package com.example.cryptopayment.infrastructure.persistence;

import com.example.cryptopayment.domain.repository.IdempotencyLock;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Profile("!redis")
public class InMemoryIdempotencyLock implements IdempotencyLock {
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Override
    public void execute(String key, Runnable action) {
        ReentrantLock lock = locks.computeIfAbsent(key, ignored -> new ReentrantLock());
        lock.lock();
        try {
            action.run();
        } finally {
            lock.unlock();
            locks.remove(key, lock);
        }
    }
}
