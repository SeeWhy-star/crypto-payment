package com.example.cryptopayment.infrastructure.persistence;

import com.example.cryptopayment.domain.model.CryptoPayment;
import com.example.cryptopayment.domain.repository.CryptoPaymentRepository;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("!mysql")
public class InMemoryCryptoPaymentRepository implements CryptoPaymentRepository {
    private final Map<String, CryptoPayment> payments = new ConcurrentHashMap<>();

    @Override
    public CryptoPayment save(CryptoPayment payment) {
        payments.put(payment.paymentNo(), payment);
        return payment;
    }

    @Override
    public Optional<CryptoPayment> findByPaymentNo(String paymentNo) {
        return Optional.ofNullable(payments.get(paymentNo));
    }
}
