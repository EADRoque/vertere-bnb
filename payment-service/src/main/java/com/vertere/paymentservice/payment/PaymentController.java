package com.vertere.paymentservice.payment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vertere.paymentservice.payment.dto.ChargeRequest;
import com.vertere.paymentservice.payment.dto.PaymentResponse;
import com.vertere.paymentservice.payment.dto.RefundRequest;
import com.vertere.paymentservice.payment.dto.RefundResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> charge(@Valid @RequestBody ChargeRequest request) {
        PaymentResponse response = paymentService.charge(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refund")
    public ResponseEntity<RefundResponse> refund(@Valid @RequestBody RefundRequest request) {
        RefundResponse response = paymentService.refund(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}