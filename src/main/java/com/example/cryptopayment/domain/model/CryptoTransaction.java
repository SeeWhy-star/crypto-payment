package com.example.cryptopayment.domain.model;

import com.example.cryptopayment.domain.enums.CryptoAsset;
import com.example.cryptopayment.domain.enums.CryptoNetwork;

import java.math.BigDecimal;

public record CryptoTransaction(
        CryptoNetwork network,
        String transactionHash,
        CryptoAsset asset,
        String fromAddress,
        String toAddress,
        BigDecimal amount,
        boolean confirmed
) {
}
