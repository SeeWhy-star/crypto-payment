package com.example.cryptopayment.application;

import com.example.cryptopayment.domain.enums.PaymentIntentStatus;
import com.example.cryptopayment.domain.enums.RefundStatus;
import com.example.cryptopayment.domain.exception.InvalidRefundException;
import com.example.cryptopayment.domain.exception.PaymentIntentNotFoundException;
import com.example.cryptopayment.domain.model.Refund;
import com.example.cryptopayment.domain.repository.PaymentIntentRepository;
import com.example.cryptopayment.domain.repository.RefundRepository;
import com.example.cryptopayment.dto.CreateRefundRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefundApplicationService {
    private final PaymentIntentRepository paymentIntentRepository;
    private final RefundRepository refundRepository;

    public RefundApplicationService(PaymentIntentRepository paymentIntentRepository,
                                    RefundRepository refundRepository) {
        this.paymentIntentRepository = paymentIntentRepository;
        this.refundRepository = refundRepository;
    }

    public Refund create(String paymentNo, CreateRefundRequest request) {
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
