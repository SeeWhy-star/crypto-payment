package com.example.cryptopayment.dto;

import com.example.cryptopayment.domain.enums.CryptoAsset;
import com.example.cryptopayment.domain.enums.CryptoNetwork;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateMockTransactionRequest(
        @NotNull CryptoNetwork network,
        @NotBlank String transactionHash,
        @NotNull CryptoAsset asset,
        @NotBlank String fromAddress,
        @NotBlank String toAddress,
        @NotNull @DecimalMin("0.00000001") BigDecimal amount,
        String tokenContract,
        long blockNumber,
        int confirmations,
        boolean receiptSuccessful
) {
}
