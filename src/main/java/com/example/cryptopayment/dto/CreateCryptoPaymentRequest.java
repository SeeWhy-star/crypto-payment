package com.example.cryptopayment.dto;

import com.example.cryptopayment.domain.enums.CryptoAsset;
import com.example.cryptopayment.domain.enums.CryptoNetwork;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateCryptoPaymentRequest(
        @NotNull CryptoAsset asset,
        @NotNull CryptoNetwork network,
        @NotBlank String depositAddress,
        @NotNull @DecimalMin("0.00000001") BigDecimal expectedAmount
) {
}
