package com.example.cryptopayment.infrastructure.webhook;

import com.example.cryptopayment.domain.model.WebhookEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Profile("webhook & !rabbitmq")
public class HttpWebhookDeliveryService implements WebhookDeliveryService {
    private static final int MAX_ATTEMPTS = 3;
    private final RestClient restClient;
    private final WebhookSigner signer;
    private final String webhookUrl;
    private final String webhookSecret;

    public HttpWebhookDeliveryService(RestClient.Builder builder, WebhookSigner signer) {
        this.restClient = builder.build();
        this.signer = signer;
        this.webhookUrl = requiredEnvironment("WEBHOOK_URL");
        this.webhookSecret = requiredEnvironment("WEBHOOK_SECRET");
    }

    @Override
    public void deliver(WebhookEvent event) {
        String signature = signer.sign(event.payload(), webhookSecret);
        RuntimeException lastFailure = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                ResponseEntity<Void> response = restClient.post()
                        .uri(webhookUrl)
                        .header("X-Webhook-Id", event.eventId())
                        .header("X-Webhook-Type", event.eventType())
                        .header("X-Webhook-Signature", signature)
                        .body(event.payload())
                        .retrieve()
                        .toBodilessEntity();
                if (response.getStatusCode().is2xxSuccessful()) {
                    return;
                }
                lastFailure = new IllegalStateException("Webhook returned " + response.getStatusCode());
            } catch (RuntimeException exception) {
                lastFailure = exception;
            }
        }
        throw new IllegalStateException("Webhook delivery failed after " + MAX_ATTEMPTS + " attempts", lastFailure);
    }

    private String requiredEnvironment(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " must be configured for the webhook profile");
        }
        return value;
    }
}
