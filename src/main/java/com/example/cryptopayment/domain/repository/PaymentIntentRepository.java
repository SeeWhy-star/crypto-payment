package com.example.cryptopayment.domain.repository;

import com.example.cryptopayment.domain.model.PaymentIntent;

import java.util.Optional;

public interface PaymentIntentRepository {

    PaymentIntent save(PaymentIntent paymentIntent);

    Optional<PaymentIntent> findByPaymentNo(String paymentNo);
}
