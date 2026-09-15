package com.example.cryptopayment.domain.repository;

public interface WebhookEventDeduplicationStore {
    boolean markIfNew(String eventId);
}
