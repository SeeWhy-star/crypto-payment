package com.example.cryptopayment.infrastructure.webhook;

import com.example.cryptopayment.domain.model.WebhookEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!webhook")
public class NoopWebhookDeliveryService implements WebhookDeliveryService {
    @Override
    public void deliver(WebhookEvent event) {
        // External delivery is disabled unless the webhook profile is explicitly enabled.
    }
}
