package com.example.cryptopayment.infrastructure.persistence.jpa;

import com.example.cryptopayment.domain.enums.PaymentIntentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment_intent")
public class PaymentIntentEntity {
    @Id
    private String paymentNo;
    private BigDecimal amount;
    private String currency;
    @Enumerated(EnumType.STRING)
    private PaymentIntentStatus status;
    private Instant createdAt;

    protected PaymentIntentEntity() {
    }

    public PaymentIntentEntity(String paymentNo, BigDecimal amount, String currency,
                               PaymentIntentStatus status, Instant createdAt) {
        this.paymentNo = paymentNo;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getPaymentNo() { return paymentNo; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public PaymentIntentStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
