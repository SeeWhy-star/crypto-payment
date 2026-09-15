package com.example.cryptopayment.controller;

import com.example.cryptopayment.application.PaymentIntentApplicationService;
import com.example.cryptopayment.dto.CreatePaymentIntentRequest;
import com.example.cryptopayment.dto.PaymentIntentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment-intents")
public class PaymentIntentController {

    private final PaymentIntentApplicationService paymentIntentService;

    public PaymentIntentController(PaymentIntentApplicationService paymentIntentService) {
        this.paymentIntentService = paymentIntentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentIntentResponse create(@Valid @RequestBody CreatePaymentIntentRequest request,
                                        @RequestHeader(value = "Idempotency-Key", required = false)
                                        String idempotencyKey) {
        return PaymentIntentResponse.from(paymentIntentService.create(request, idempotencyKey));
    }

    @GetMapping("/{paymentNo}")
    public PaymentIntentResponse get(@PathVariable String paymentNo) {
        return PaymentIntentResponse.from(paymentIntentService.getByPaymentNo(paymentNo));
    }
}
