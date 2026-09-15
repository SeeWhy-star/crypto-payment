package com.example.cryptopayment.infrastructure.persistence;

import com.example.cryptopayment.domain.model.Refund;
import com.example.cryptopayment.domain.repository.RefundRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

@Repository
@Profile("!mysql")
public class InMemoryRefundRepository implements RefundRepository {
    private final Map<String, CopyOnWriteArrayList<Refund>> refunds = new ConcurrentHashMap<>();

    @Override
    public Refund save(Refund refund) {
        refunds.computeIfAbsent(refund.paymentNo(), ignored -> new CopyOnWriteArrayList<>()).add(refund);
        return refund;
    }

    @Override
    public List<Refund> findByPaymentNo(String paymentNo) {
        return List.copyOf(refunds.getOrDefault(paymentNo, new CopyOnWriteArrayList<>()));
    }

    @Override
    public Optional<Refund> findByRefundNo(String refundNo) {
        return refunds.values().stream().flatMap(List::stream)
                .filter(refund -> refund.refundNo().equals(refundNo)).findFirst();
    }
}
