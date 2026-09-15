package com.example.cryptopayment.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataRefundRepository extends JpaRepository<RefundEntity, String> {
    List<RefundEntity> findByPaymentNo(String paymentNo);

    Optional<RefundEntity> findByRefundNo(String refundNo);
}
