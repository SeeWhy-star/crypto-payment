package com.example.cryptopayment.controller;

import com.example.cryptopayment.application.CryptoPaymentApplicationService;
import com.example.cryptopayment.domain.model.CryptoTransaction;
import com.example.cryptopayment.dto.CreateCryptoPaymentRequest;
import com.example.cryptopayment.dto.CreateMockTransactionRequest;
import com.example.cryptopayment.dto.CryptoPaymentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CryptoPaymentController {
    private final CryptoPaymentApplicationService service;

    public CryptoPaymentController(CryptoPaymentApplicationService service) {
        this.service = service;
    }

    @PostMapping("/api/payment-intents/{paymentNo}/crypto-payment")
    @ResponseStatus(HttpStatus.CREATED)
    public CryptoPaymentResponse configure(@PathVariable String paymentNo,
                                           @Valid @RequestBody CreateCryptoPaymentRequest request) {
        return CryptoPaymentResponse.from(service.configure(paymentNo, request));
    }

    @PostMapping("/api/payment-intents/{paymentNo}/refresh")
    public CryptoPaymentResponse refresh(@PathVariable String paymentNo) {
        return CryptoPaymentResponse.from(service.refresh(paymentNo));
    }

    @PostMapping("/api/mock/blockchain/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public void recordTransaction(@Valid @RequestBody CreateMockTransactionRequest request) {
        service.recordMockTransaction(new CryptoTransaction(request.network(), request.transactionHash(),
                request.asset(), request.fromAddress(), request.toAddress(), request.amount(), request.confirmed()));
    }
}
