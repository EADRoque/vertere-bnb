package com.vertere.paymentservice.payment;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.vertere.paymentservice.payment.dto.ChargeRequest;
import com.vertere.paymentservice.payment.dto.PaymentResponse;
import com.vertere.paymentservice.payment.dto.RefundRequest;
import com.vertere.paymentservice.payment.dto.RefundResponse;
import com.vertere.paymentservice.payment.exception.PaymentNotFoundException;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final Random random = new Random();

    public PaymentService(PaymentRepository paymentRepository, RefundRepository refundRepository) {
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
    }

    public PaymentResponse charge(ChargeRequest request) {
        boolean succeeds = random.nextInt(100) < 90;
        String status = succeeds ? "SUCCEEDED" : "DECLINED";

        Payment payment = new Payment(request.bookingId(), request.amount(), "USD", status);
        Payment saved = paymentRepository.save(payment);

        return toResponse(saved);
    }

    public RefundResponse refund(RefundRequest request) {
        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        Refund refundRecord = new Refund(payment.getId(), payment.getAmount(), "SUCCEEDED");
        Refund saved = refundRepository.save(refundRecord);

        return new RefundResponse(
                saved.getId(),
                saved.getPaymentId(),
                saved.getAmount(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getBookingId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }

}