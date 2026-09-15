package com.example.cryptopayment.application;

import com.example.cryptopayment.domain.enums.PaymentIntentStatus;
import com.example.cryptopayment.domain.exception.PaymentIntentNotFoundException;
import com.example.cryptopayment.domain.model.PaymentIntent;
import com.example.cryptopayment.domain.repository.PaymentIntentRepository;
import com.example.cryptopayment.dto.CreatePaymentIntentRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentIntentApplicationService {

    private final PaymentIntentRepository paymentIntentRepository;

    public PaymentIntentApplicationService(PaymentIntentRepository paymentIntentRepository) {
        this.paymentIntentRepository = paymentIntentRepository;
    }

    public PaymentIntent create(CreatePaymentIntentRequest request) {
        PaymentIntent paymentIntent = new PaymentIntent(
                "pi_" + UUID.randomUUID().toString().replace("-", ""),
                request.amount(),
                request.currency().trim().toUpperCase(),
                PaymentIntentStatus.CREATED,
                Instant.now()
        );
        return paymentIntentRepository.save(paymentIntent);
    }

    public PaymentIntent getByPaymentNo(String paymentNo) {
        return paymentIntentRepository.findByPaymentNo(paymentNo)
                .orElseThrow(() -> new PaymentIntentNotFoundException(paymentNo));
    }
}
