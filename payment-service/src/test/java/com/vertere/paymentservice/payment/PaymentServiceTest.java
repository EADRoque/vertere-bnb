package com.vertere.paymentservice.payment;  //which folder/namespace this class belongs to

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vertere.paymentservice.payment.dto.ChargeRequest;   //the shape of an incoming "charge this booking" request
import com.vertere.paymentservice.payment.dto.PaymentResponse;   //the shape of what we get back about a payment

/**
 * This class checks that PaymentService's business logic actually
 * behaves the way it's supposed to - without touching a real database
 * or a real payment processor.
 *
 * - paymentRepository: fake ("mock") stand-in for the real database, so
 *   we can control exactly what it returns instead of relying on a real
 *   one.
 * - paymentService: the actual class under test; @InjectMocks builds it
 *   automatically and wires in the mock above.
 * - bookingId: a fresh random booking id generated before every test.
 * - charge_alwaysSavesAValidPayment_regardlessOfOutcome: a single charge
 *   should always save a payment with the right booking id and amount,
 *   whether it's approved or declined.
 * - charge_producesBothOutcomes_acrossManyAttempts: since charge() uses
 *   randomness to simulate a real processor (~90% approval), running it
 *   many times should eventually produce both a SUCCEEDED and a DECLINED
 *   result - this is the test's way of proving both code paths actually
 *   work, since a single run can't guarantee hitting both.
 */
@ExtendWith(MockitoExtension.class)   //tells the testing framework to set up the @Mock/@InjectMocks fields automatically
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;   //fake stand-in for the payments database

    @InjectMocks
    private PaymentService paymentService;   //the real class we're testing, auto-built with the mock above injected in

    private UUID bookingId;

    @BeforeEach
    void setUp() {
        bookingId = UUID.randomUUID();   //fresh random booking id before every test so tests don't affect each other
    }

    @Test
    void charge_alwaysSavesAValidPayment_regardlessOfOutcome() {
        ChargeRequest request = new ChargeRequest(bookingId, new BigDecimal("250.00"));

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));   //tell the fake database to just hand back whatever it was given

        PaymentResponse response = paymentService.charge(request);   //actually run the real charge logic

        assertEquals(bookingId, response.bookingId());   //check the booking id came back correctly
        assertEquals(new BigDecimal("250.00"), response.amount());   //check the amount came back correctly
        assertTrue(List.of("SUCCEEDED", "DECLINED").contains(response.status()));   //status should always be one of these two, whichever the random outcome was

        verify(paymentRepository).save(any(Payment.class));   //confirm the payment attempt was actually saved
    }

    @Test
    void charge_producesBothOutcomes_acrossManyAttempts() {
        ChargeRequest request = new ChargeRequest(bookingId, new BigDecimal("250.00"));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean sawSucceeded = false;
        boolean sawDeclined = false;

        for (int i = 0; i < 200; i++) {   //run enough attempts that both a ~90% and a ~10% outcome are virtually guaranteed to show up
            PaymentResponse response = paymentService.charge(request);
            if (response.status().equals("SUCCEEDED")) sawSucceeded = true;
            if (response.status().equals("DECLINED")) sawDeclined = true;
        }

        assertTrue(sawSucceeded, "Expected at least one SUCCEEDED outcome across 200 attempts");
        assertTrue(sawDeclined, "Expected at least one DECLINED outcome across 200 attempts");
    }

}