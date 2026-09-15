package com.example.cryptopayment.domain.model;

import com.example.cryptopayment.domain.enums.CryptoAsset;
import com.example.cryptopayment.domain.enums.CryptoNetwork;
import com.example.cryptopayment.domain.enums.CryptoPaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record CryptoPayment(
        String paymentNo,
        CryptoAsset asset,
        CryptoNetwork network,
        String depositAddress,
        BigDecimal expectedAmount,
        String tokenContract,
        int requiredConfirmations,
        String transactionHash,
        CryptoPaymentStatus status,
        Instant createdAt
) {
    public CryptoPayment withTransaction(String hash, CryptoPaymentStatus newStatus) {
        return new CryptoPayment(paymentNo, asset, network, depositAddress, expectedAmount,
                tokenContract, requiredConfirmations, hash, newStatus, createdAt);
    }
}
