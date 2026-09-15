package com.example.cryptopayment.application;

import com.example.cryptopayment.domain.enums.PaymentIntentStatus;
import com.example.cryptopayment.domain.enums.RefundStatus;
import com.example.cryptopayment.domain.exception.InvalidRefundException;
import com.example.cryptopayment.domain.exception.PaymentIntentNotFoundException;
import com.example.cryptopayment.domain.model.Refund;
import com.example.cryptopayment.domain.repository.PaymentIntentRepository;
import com.example.cryptopayment.domain.repository.RefundRepository;
import com.example.cryptopayment.domain.repository.RefundIdempotencyStore;
import com.example.cryptopayment.domain.repository.IdempotencyLock;
import com.example.cryptopayment.dto.CreateRefundRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefundApplicationService {
    private final PaymentIntentRepository paymentIntentRepository;
    private final RefundRepository refundRepository;
    private final RefundIdempotencyStore idempotencyStore;
    private final IdempotencyLock idempotencyLock;

    public RefundApplicationService(PaymentIntentRepository paymentIntentRepository,
                                    RefundRepository refundRepository,
                                    RefundIdempotencyStore idempotencyStore,
                                    IdempotencyLock idempotencyLock) {
        this.paymentIntentRepository = paymentIntentRepository;
        this.refundRepository = refundRepository;
        this.idempotencyStore = idempotencyStore;
        this.idempotencyLock = idempotencyLock;
    }

    public Refund create(String paymentNo, CreateRefundRequest request) {
        return create(paymentNo, request, null);
    }

    public Refund create(String paymentNo, CreateRefundRequest request, String idempotencyKey) {
        final Refund[] result = new Refund[1];
        idempotencyLock.execute("refund:payment:" + paymentNo, () -> {
            if (idempotencyKey == null || idempotencyKey.isBlank()) {
                result[0] = createRefund(paymentNo, request);
                return;
            }
            String scopedKey = paymentNo + ":" + idempotencyKey;
            result[0] = createWithIdempotency(paymentNo, request, scopedKey);
        });
        return result[0];
    }

    private Refund createWithIdempotency(String paymentNo, CreateRefundRequest request, String key) {
        var existing = idempotencyStore.findRefundNo(key)
                .flatMap(refundRepository::findByRefundNo);
        if (existing.isPresent()) {
            if (existing.get().amount().compareTo(request.amount()) != 0) {
                throw new InvalidRefundException("Idempotency key was already used for another refund amount");
            }
            return existing.get();
        }
        Refund refund = createRefund(paymentNo, request);
        idempotencyStore.save(key, refund.refundNo());
        return refund;
    }

    private Refund createRefund(String paymentNo, CreateRefundRequest request) {
        var payment = paymentIntentRepository.findByPaymentNo(paymentNo)
                .orElseThrow(() -> new PaymentIntentNotFoundException(paymentNo));
        if (payment.status() != PaymentIntentStatus.SUCCEEDED) {
            throw new InvalidRefundException("Only succeeded payments can be refunded");
        }
        var refunded = refundRepository.sumSucceededAmount(paymentNo);
        if (refunded.add(request.amount()).compareTo(payment.amount()) > 0) {
            throw new InvalidRefundException("Refund amount exceeds refundable amount");
        }
        Refund refund = new Refund("re_" + UUID.randomUUID().toString().replace("-", ""),
                paymentNo, request.amount(), RefundStatus.REQUESTED, Instant.now());
        // Mock refund channel completes immediately; a real provider will become asynchronous later.
        Refund completed = new Refund(refund.refundNo(), refund.paymentNo(), refund.amount(),
                RefundStatus.SUCCEEDED, refund.createdAt());
        return refundRepository.save(completed);
    }
}
