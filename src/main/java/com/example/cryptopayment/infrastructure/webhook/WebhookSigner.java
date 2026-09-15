package com.example.cryptopayment.infrastructure.webhook;

public interface WebhookSigner {
    String sign(String payload, String secret);
}
