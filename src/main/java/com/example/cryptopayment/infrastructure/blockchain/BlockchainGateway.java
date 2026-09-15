package com.example.cryptopayment.infrastructure.blockchain;

import com.example.cryptopayment.domain.enums.CryptoNetwork;
import com.example.cryptopayment.domain.enums.CryptoAsset;
import com.example.cryptopayment.domain.model.CryptoTransaction;

import java.util.Optional;
import java.math.BigDecimal;

public interface BlockchainGateway {
    Optional<CryptoTransaction> findTransaction(CryptoNetwork network, String transactionHash);

    default Optional<CryptoTransaction> findTransaction(CryptoNetwork network, String transactionHash,
                                                         CryptoAsset asset, String tokenContract) {
        return findTransaction(network, transactionHash);
    }

    Optional<CryptoTransaction> findMatchingTransaction(CryptoNetwork network, CryptoAsset asset,
                                                         String tokenContract, String toAddress, BigDecimal amount);

    void recordTransaction(CryptoTransaction transaction);
}
