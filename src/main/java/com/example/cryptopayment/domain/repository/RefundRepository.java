package com.example.cryptopayment.domain.repository;

import com.example.cryptopayment.domain.model.Refund;
import com.example.cryptopayment.domain.enums.RefundStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RefundRepository {
    Refund save(Refund refund);

    List<Refund> findByPaymentNo(String paymentNo);

    Optional<Refund> findByRefundNo(String refundNo);

    default BigDecimal sumSucceededAmount(String paymentNo) {
        return findByPaymentNo(paymentNo).stream()
                .filter(refund -> refund.status() == RefundStatus.SUCCEEDED)
                .map(Refund::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
