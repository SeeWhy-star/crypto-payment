package com.example.cryptopayment.application;

import com.example.cryptopayment.domain.enums.PaymentIntentStatus;
import com.example.cryptopayment.domain.exception.PaymentIntentNotFoundException;
import com.example.cryptopayment.domain.model.PaymentIntent;
import com.example.cryptopayment.domain.repository.PaymentIntentRepository;
import com.example.cryptopayment.domain.repository.IdempotencyStore;
import com.example.cryptopayment.dto.CreatePaymentIntentRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentIntentApplicationService {

    private final PaymentIntentRepository paymentIntentRepository;
    private final IdempotencyStore idempotencyStore;

    public PaymentIntentApplicationService(PaymentIntentRepository paymentIntentRepository,
                                           IdempotencyStore idempotencyStore) {
        this.paymentIntentRepository = paymentIntentRepository;
        this.idempotencyStore = idempotencyStore;
    }

    public PaymentIntent create(CreatePaymentIntentRequest request, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<String> existingPaymentNo = idempotencyStore.findPaymentNo(idempotencyKey);
            if (existingPaymentNo.isPresent()) {
                return getByPaymentNo(existingPaymentNo.get());
            }
        }
        PaymentIntent paymentIntent = new PaymentIntent(
                "pi_" + UUID.randomUUID().toString().replace("-", ""),
                request.amount(),
                request.currency().trim().toUpperCase(),
                PaymentIntentStatus.CREATED,
                Instant.now()
        );
        PaymentIntent saved = paymentIntentRepository.save(paymentIntent);
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyStore.save(idempotencyKey, saved.paymentNo());
        }
        return saved;
    }

    public PaymentIntent getByPaymentNo(String paymentNo) {
        return paymentIntentRepository.findByPaymentNo(paymentNo)
                .orElseThrow(() -> new PaymentIntentNotFoundException(paymentNo));
    }
}
