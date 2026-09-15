package com.example.cryptopayment.infrastructure.persistence.jpa;

import com.example.cryptopayment.domain.enums.CryptoAsset;
import com.example.cryptopayment.domain.enums.CryptoNetwork;
import com.example.cryptopayment.domain.enums.CryptoPaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "crypto_payment", uniqueConstraints = @UniqueConstraint(name = "uk_crypto_payment_tx_hash", columnNames = "transactionHash"))
public class CryptoPaymentEntity {
    @Id
    private String paymentNo;
    @Enumerated(EnumType.STRING)
    private CryptoAsset asset;
    @Enumerated(EnumType.STRING)
    private CryptoNetwork network;
    private String depositAddress;
    private BigDecimal expectedAmount;
    private String tokenContract;
    private int requiredConfirmations;
    private String transactionHash;
    @Enumerated(EnumType.STRING)
    private CryptoPaymentStatus status;
    private Instant createdAt;

    protected CryptoPaymentEntity() {
    }

    public CryptoPaymentEntity(String paymentNo, CryptoAsset asset, CryptoNetwork network,
                               String depositAddress, BigDecimal expectedAmount, String tokenContract,
                               int requiredConfirmations, String transactionHash,
                               CryptoPaymentStatus status, Instant createdAt) {
        this.paymentNo = paymentNo;
        this.asset = asset;
        this.network = network;
        this.depositAddress = depositAddress;
        this.expectedAmount = expectedAmount;
        this.tokenContract = tokenContract;
        this.requiredConfirmations = requiredConfirmations;
        this.transactionHash = transactionHash;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getPaymentNo() { return paymentNo; }
    public CryptoAsset getAsset() { return asset; }
    public CryptoNetwork getNetwork() { return network; }
    public String getDepositAddress() { return depositAddress; }
    public BigDecimal getExpectedAmount() { return expectedAmount; }
    public String getTokenContract() { return tokenContract; }
    public int getRequiredConfirmations() { return requiredConfirmations; }
    public String getTransactionHash() { return transactionHash; }
    public CryptoPaymentStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
