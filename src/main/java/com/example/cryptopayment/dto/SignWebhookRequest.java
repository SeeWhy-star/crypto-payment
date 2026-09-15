package com.example.cryptopayment.dto;

import jakarta.validation.constraints.NotBlank;

public record SignWebhookRequest(
        @NotBlank String payload,
        @NotBlank String secret
) {
}
