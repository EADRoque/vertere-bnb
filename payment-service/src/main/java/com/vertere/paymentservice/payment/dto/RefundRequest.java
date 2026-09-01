package com.vertere.paymentservice.payment.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record RefundRequest(
        @NotNull
        UUID paymentId
) {}