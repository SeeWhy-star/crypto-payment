package com.example.cryptopayment.application;

import com.example.cryptopayment.domain.enums.CryptoPaymentStatus;
import com.example.cryptopayment.domain.enums.PaymentIntentStatus;
import com.example.cryptopayment.domain.exception.CryptoPaymentNotFoundException;
import com.example.cryptopayment.domain.exception.PaymentIntentNotFoundException;
import com.example.cryptopayment.domain.model.CryptoPayment;
import com.example.cryptopayment.domain.model.CryptoTransaction;
import com.example.cryptopayment.domain.repository.CryptoPaymentRepository;
import com.example.cryptopayment.domain.repository.PaymentIntentRepository;
import com.example.cryptopayment.infrastructure.blockchain.BlockchainGateway;
import com.example.cryptopayment.dto.CreateCryptoPaymentRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CryptoPaymentApplicationService {
    private final CryptoPaymentRepository cryptoPaymentRepository;
    private final PaymentIntentRepository paymentIntentRepository;
    private final BlockchainGateway blockchainGateway;

    public CryptoPaymentApplicationService(CryptoPaymentRepository cryptoPaymentRepository,
                                            PaymentIntentRepository paymentIntentRepository,
                                            BlockchainGateway blockchainGateway) {
        this.cryptoPaymentRepository = cryptoPaymentRepository;
        this.paymentIntentRepository = paymentIntentRepository;
        this.blockchainGateway = blockchainGateway;
    }

    public CryptoPayment configure(String paymentNo, CreateCryptoPaymentRequest request) {
        if (paymentIntentRepository.findByPaymentNo(paymentNo).isEmpty()) {
            throw new PaymentIntentNotFoundException(paymentNo);
        }
        CryptoPayment payment = new CryptoPayment(paymentNo, request.asset(), request.network(),
                request.depositAddress().trim(), request.expectedAmount(), null,
                CryptoPaymentStatus.WAITING_PAYMENT, Instant.now());
        return cryptoPaymentRepository.save(payment);
    }

    public CryptoPayment refresh(String paymentNo) {
        CryptoPayment payment = cryptoPaymentRepository.findByPaymentNo(paymentNo)
                .orElseThrow(() -> new CryptoPaymentNotFoundException(paymentNo));
        CryptoTransaction transaction = payment.transactionHash() == null
                ? blockchainGateway.findMatchingTransaction(payment.network(), payment.asset(),
                payment.depositAddress(), payment.expectedAmount()).orElse(null)
                : blockchainGateway.findTransaction(payment.network(), payment.transactionHash()).orElse(null);
        if (transaction == null) {
            return payment;
        }
        CryptoPayment paymentWithTransaction = payment.transactionHash() == null
                ? payment.withTransaction(transaction.transactionHash(), CryptoPaymentStatus.DETECTED) : payment;
        CryptoTransaction matchedTransaction = blockchainGateway.findTransaction(payment.network(),
                paymentWithTransaction.transactionHash()).orElse(transaction);
        CryptoPaymentStatus status = matches(paymentWithTransaction, matchedTransaction)
                ? (matchedTransaction.confirmed() ? CryptoPaymentStatus.SUCCEEDED : CryptoPaymentStatus.CONFIRMING)
                : CryptoPaymentStatus.FAILED;
        CryptoPayment updated = cryptoPaymentRepository.save(paymentWithTransaction.withTransaction(
                paymentWithTransaction.transactionHash(), status));
        paymentIntentRepository.findByPaymentNo(paymentNo).ifPresent(intent -> {
            PaymentIntentStatus intentStatus = status == CryptoPaymentStatus.SUCCEEDED
                    ? PaymentIntentStatus.SUCCEEDED
                    : status == CryptoPaymentStatus.FAILED ? PaymentIntentStatus.FAILED : PaymentIntentStatus.PROCESSING;
            paymentIntentRepository.save(intent.withStatus(intentStatus));
        });
        return updated;
    }

    public void recordMockTransaction(CryptoTransaction transaction) {
        blockchainGateway.recordTransaction(transaction);
    }

    private boolean matches(CryptoPayment payment, CryptoTransaction transaction) {
        return payment.asset() == transaction.asset()
                && payment.depositAddress().equalsIgnoreCase(transaction.toAddress())
                && payment.expectedAmount().compareTo(transaction.amount()) == 0;
    }
}
