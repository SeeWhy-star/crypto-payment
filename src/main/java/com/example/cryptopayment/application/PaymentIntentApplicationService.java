package com.example.cryptopayment.application;

import com.example.cryptopayment.domain.enums.PaymentIntentStatus;
import com.example.cryptopayment.domain.exception.PaymentIntentNotFoundException;
import com.example.cryptopayment.domain.model.PaymentIntent;
import com.example.cryptopayment.domain.repository.PaymentIntentRepository;
import com.example.cryptopayment.domain.repository.IdempotencyStore;
import com.example.cryptopayment.domain.repository.IdempotencyLock;
import com.example.cryptopayment.dto.CreatePaymentIntentRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentIntentApplicationService {

    private final PaymentIntentRepository paymentIntentRepository;
    private final IdempotencyStore idempotencyStore;
    private final IdempotencyLock idempotencyLock;

    public PaymentIntentApplicationService(PaymentIntentRepository paymentIntentRepository,
                                           IdempotencyStore idempotencyStore,
                                           IdempotencyLock idempotencyLock) {
        this.paymentIntentRepository = paymentIntentRepository;
        this.idempotencyStore = idempotencyStore;
        this.idempotencyLock = idempotencyLock;
    }

    public PaymentIntent create(CreatePaymentIntentRequest request, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return createPaymentIntent(request);
        }
        final PaymentIntent[] result = new PaymentIntent[1];
        idempotencyLock.execute(idempotencyKey, () -> result[0] = createWithIdempotency(request, idempotencyKey));
        return result[0];
    }

    private PaymentIntent createWithIdempotency(CreatePaymentIntentRequest request, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<String> existingPaymentNo = idempotencyStore.findPaymentNo(idempotencyKey);
            if (existingPaymentNo.isPresent()) {
                return getByPaymentNo(existingPaymentNo.get());
            }
        }
        PaymentIntent saved = createPaymentIntent(request);
        idempotencyStore.save(idempotencyKey, saved.paymentNo());
        return saved;
    }

    private PaymentIntent createPaymentIntent(CreatePaymentIntentRequest request) {
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
