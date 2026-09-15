package com.example.cryptopayment.infrastructure.persistence.jpa;

import com.example.cryptopayment.domain.enums.RefundStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "refund")
public class RefundEntity {
    @Id
    private String refundNo;
    private String paymentNo;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private RefundStatus status;
    private Instant createdAt;

    protected RefundEntity() {
    }

    public RefundEntity(String refundNo, String paymentNo, BigDecimal amount,
                        RefundStatus status, Instant createdAt) {
        this.refundNo = refundNo;
        this.paymentNo = paymentNo;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getRefundNo() { return refundNo; }
    public String getPaymentNo() { return paymentNo; }
    public BigDecimal getAmount() { return amount; }
    public RefundStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
