package com.example.cryptopayment.infrastructure.redis;

import com.example.cryptopayment.domain.repository.RefundIdempotencyStore;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@Profile("redis")
public class RedisRefundIdempotencyStore implements RefundIdempotencyStore {
    private static final Duration KEY_RETENTION = Duration.ofHours(24);
    private final StringRedisTemplate redisTemplate;

    public RedisRefundIdempotencyStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<String> findRefundNo(String idempotencyKey) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(redisKey(idempotencyKey)));
    }

    @Override
    public void save(String idempotencyKey, String refundNo) {
        redisTemplate.opsForValue().setIfAbsent(redisKey(idempotencyKey), refundNo, KEY_RETENTION);
    }

    private String redisKey(String idempotencyKey) {
        return "refund:idempotency:" + idempotencyKey;
    }
}
