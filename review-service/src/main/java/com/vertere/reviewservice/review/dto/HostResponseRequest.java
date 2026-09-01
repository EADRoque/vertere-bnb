package com.vertere.reviewservice.review.dto;

import jakarta.validation.constraints.NotBlank;

public record HostResponseRequest(
        @NotBlank
        String response
) {}