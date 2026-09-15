package com.example.cryptopayment.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCryptoPaymentRepository extends JpaRepository<CryptoPaymentEntity, String> {
}
