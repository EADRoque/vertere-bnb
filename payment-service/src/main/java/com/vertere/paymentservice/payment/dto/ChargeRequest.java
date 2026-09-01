package com.vertere.paymentservice.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record ChargeRequest(

        @NotNull
        UUID bookingId,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount

) {}