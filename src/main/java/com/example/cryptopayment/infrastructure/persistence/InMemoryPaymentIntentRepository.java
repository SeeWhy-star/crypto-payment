package com.example.cryptopayment.infrastructure.persistence;

import com.example.cryptopayment.domain.model.PaymentIntent;
import com.example.cryptopayment.domain.repository.PaymentIntentRepository;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("!mysql")
public class InMemoryPaymentIntentRepository implements PaymentIntentRepository {

    private final Map<String, PaymentIntent> paymentIntents = new ConcurrentHashMap<>();

    @Override
    public PaymentIntent save(PaymentIntent paymentIntent) {
        paymentIntents.put(paymentIntent.paymentNo(), paymentIntent);
        return paymentIntent;
    }

    @Override
    public Optional<PaymentIntent> findByPaymentNo(String paymentNo) {
        return Optional.ofNullable(paymentIntents.get(paymentNo));
    }
}
