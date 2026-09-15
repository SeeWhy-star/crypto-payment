package com.example.cryptopayment.infrastructure.redis;

import com.example.cryptopayment.domain.repository.WebhookEventDeduplicationStore;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Profile("redis")
public class RedisWebhookEventDeduplicationStore implements WebhookEventDeduplicationStore {
    private static final Duration RETENTION = Duration.ofDays(7);
    private final StringRedisTemplate redisTemplate;

    public RedisWebhookEventDeduplicationStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean markIfNew(String eventId) {
        Boolean created = redisTemplate.opsForValue()
                .setIfAbsent("webhook:event:processed:" + eventId, "1", RETENTION);
        return Boolean.TRUE.equals(created);
    }
}
