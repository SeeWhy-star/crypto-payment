package com.example.cryptopayment.infrastructure.persistence.jpa;

import com.example.cryptopayment.domain.model.PaymentIntent;
import com.example.cryptopayment.domain.repository.PaymentIntentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("mysql")
public class JpaPaymentIntentRepository implements PaymentIntentRepository {
    private final SpringDataPaymentIntentRepository delegate;

    public JpaPaymentIntentRepository(SpringDataPaymentIntentRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public PaymentIntent save(PaymentIntent paymentIntent) {
        PaymentIntentEntity entity = new PaymentIntentEntity(paymentIntent.paymentNo(), paymentIntent.amount(),
                paymentIntent.currency(), paymentIntent.status(), paymentIntent.createdAt());
        PaymentIntentEntity saved = delegate.save(entity);
        return new PaymentIntent(saved.getPaymentNo(), saved.getAmount(), saved.getCurrency(),
                saved.getStatus(), saved.getCreatedAt());
    }

    @Override
    public Optional<PaymentIntent> findByPaymentNo(String paymentNo) {
        return delegate.findById(paymentNo).map(entity -> new PaymentIntent(entity.getPaymentNo(),
                entity.getAmount(), entity.getCurrency(), entity.getStatus(), entity.getCreatedAt()));
    }
}
