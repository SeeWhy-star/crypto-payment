package com.example.cryptopayment.infrastructure.blockchain;

import com.example.cryptopayment.domain.enums.CryptoNetwork;
import com.example.cryptopayment.domain.enums.CryptoAsset;
import com.example.cryptopayment.domain.model.CryptoTransaction;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.math.BigDecimal;

@Component
public class MockBlockchainGateway implements BlockchainGateway {
    private final Map<String, CryptoTransaction> transactions = new ConcurrentHashMap<>();

    @Override
    public Optional<CryptoTransaction> findTransaction(CryptoNetwork network, String transactionHash) {
        return Optional.ofNullable(transactions.get(transactionHash))
                .filter(transaction -> transaction.network() == network);
    }

    @Override
    public Optional<CryptoTransaction> findMatchingTransaction(CryptoNetwork network, CryptoAsset asset,
                                                                 String tokenContract, String toAddress, BigDecimal amount) {
        return transactions.values().stream()
                .filter(transaction -> transaction.network() == network)
                .filter(transaction -> transaction.asset() == asset)
                .filter(transaction -> java.util.Objects.equals(transaction.tokenContract(), tokenContract))
                .filter(transaction -> transaction.toAddress().equalsIgnoreCase(toAddress))
                .filter(transaction -> transaction.amount().compareTo(amount) == 0)
                .findFirst();
    }

    @Override
    public void recordTransaction(CryptoTransaction transaction) {
        transactions.put(transaction.transactionHash(), transaction);
    }
}
