package com.example.cryptopayment.dto;

import com.example.cryptopayment.domain.enums.CryptoAsset;
import com.example.cryptopayment.domain.enums.CryptoNetwork;
import com.example.cryptopayment.domain.enums.CryptoPaymentStatus;
import com.example.cryptopayment.domain.model.CryptoPayment;

import java.math.BigDecimal;
import java.time.Instant;

public record CryptoPaymentResponse(
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
    public static CryptoPaymentResponse from(CryptoPayment payment) {
        return new CryptoPaymentResponse(payment.paymentNo(), payment.asset(), payment.network(),
                payment.depositAddress(), payment.expectedAmount(), payment.tokenContract(),
                payment.requiredConfirmations(), payment.transactionHash(),
                payment.status(), payment.createdAt());
    }
}
