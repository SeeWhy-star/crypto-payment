package com.example.cryptopayment.infrastructure.redis;

import com.example.cryptopayment.domain.repository.IdempotencyLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Profile("redis")
public class RedisIdempotencyLock implements IdempotencyLock {
    private final RedissonClient redissonClient;

    public RedisIdempotencyLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void execute(String key, Runnable action) {
        RLock lock = redissonClient.getLock("payment:idempotency:lock:" + key);
        try {
            if (!lock.tryLock(5, 30, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Could not acquire idempotency lock");
            }
            action.run();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while acquiring idempotency lock", exception);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
