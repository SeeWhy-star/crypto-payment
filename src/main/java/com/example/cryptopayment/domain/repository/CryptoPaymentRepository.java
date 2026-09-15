package com.example.cryptopayment.domain.repository;

import com.example.cryptopayment.domain.model.CryptoPayment;

import java.util.Optional;

public interface CryptoPaymentRepository {
    CryptoPayment save(CryptoPayment payment);

    Optional<CryptoPayment> findByPaymentNo(String paymentNo);
}
