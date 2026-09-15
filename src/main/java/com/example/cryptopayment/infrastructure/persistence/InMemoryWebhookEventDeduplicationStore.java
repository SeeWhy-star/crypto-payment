package com.example.cryptopayment.infrastructure.persistence;

import com.example.cryptopayment.domain.repository.WebhookEventDeduplicationStore;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("!redis")
public class InMemoryWebhookEventDeduplicationStore implements WebhookEventDeduplicationStore {
    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    @Override
    public boolean markIfNew(String eventId) {
        return processedEvents.add(eventId);
    }
}
