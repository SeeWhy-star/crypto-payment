package com.example.cryptopayment.controller;

import com.example.cryptopayment.dto.SignWebhookRequest;
import com.example.cryptopayment.infrastructure.webhook.WebhookSigner;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {
    private final WebhookSigner webhookSigner;

    public WebhookController(WebhookSigner webhookSigner) {
        this.webhookSigner = webhookSigner;
    }

    @PostMapping("/sign")
    public Map<String, String> sign(@Valid @RequestBody SignWebhookRequest request) {
        return Map.of("signature", webhookSigner.sign(request.payload(), request.secret()));
    }
}
