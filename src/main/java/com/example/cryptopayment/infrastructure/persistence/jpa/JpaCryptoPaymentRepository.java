package com.example.cryptopayment.infrastructure.persistence.jpa;

import com.example.cryptopayment.domain.model.CryptoPayment;
import com.example.cryptopayment.domain.repository.CryptoPaymentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("mysql")
public class JpaCryptoPaymentRepository implements CryptoPaymentRepository {
    private final SpringDataCryptoPaymentRepository delegate;

    public JpaCryptoPaymentRepository(SpringDataCryptoPaymentRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public CryptoPayment save(CryptoPayment payment) {
        CryptoPaymentEntity entity = new CryptoPaymentEntity(payment.paymentNo(), payment.asset(), payment.network(),
                payment.depositAddress(), payment.expectedAmount(), payment.tokenContract(),
                payment.requiredConfirmations(), payment.transactionHash(), payment.status(), payment.createdAt());
        CryptoPaymentEntity saved = delegate.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<CryptoPayment> findByPaymentNo(String paymentNo) {
        return delegate.findById(paymentNo).map(this::toDomain);
    }

    private CryptoPayment toDomain(CryptoPaymentEntity entity) {
        return new CryptoPayment(entity.getPaymentNo(), entity.getAsset(), entity.getNetwork(),
                entity.getDepositAddress(), entity.getExpectedAmount(), entity.getTokenContract(),
                entity.getRequiredConfirmations(), entity.getTransactionHash(), entity.getStatus(), entity.getCreatedAt());
    }
}
