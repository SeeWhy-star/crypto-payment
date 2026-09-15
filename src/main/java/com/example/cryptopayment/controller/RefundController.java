package com.example.cryptopayment.controller;

import com.example.cryptopayment.application.RefundApplicationService;
import com.example.cryptopayment.dto.CreateRefundRequest;
import com.example.cryptopayment.dto.RefundResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RefundController {
    private final RefundApplicationService refundService;

    public RefundController(RefundApplicationService refundService) {
        this.refundService = refundService;
    }

    @PostMapping("/api/payment-intents/{paymentNo}/refunds")
    @ResponseStatus(HttpStatus.CREATED)
    public RefundResponse create(@PathVariable String paymentNo,
                                 @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
                                 @Valid @RequestBody CreateRefundRequest request) {
        return RefundResponse.from(refundService.create(paymentNo, request, idempotencyKey));
    }
}
