package com.example.cryptopayment.infrastructure.persistence.jpa;

import com.example.cryptopayment.domain.model.Refund;
import com.example.cryptopayment.domain.repository.RefundRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("mysql")
public class JpaRefundRepository implements RefundRepository {
    private final SpringDataRefundRepository delegate;

    public JpaRefundRepository(SpringDataRefundRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public Refund save(Refund refund) {
        RefundEntity saved = delegate.save(new RefundEntity(refund.refundNo(), refund.paymentNo(),
                refund.amount(), refund.status(), refund.createdAt()));
        return toDomain(saved);
    }

    @Override
    public List<Refund> findByPaymentNo(String paymentNo) {
        return delegate.findByPaymentNo(paymentNo).stream().map(this::toDomain).toList();
    }

    private Refund toDomain(RefundEntity entity) {
        return new Refund(entity.getRefundNo(), entity.getPaymentNo(), entity.getAmount(),
                entity.getStatus(), entity.getCreatedAt());
    }
}
