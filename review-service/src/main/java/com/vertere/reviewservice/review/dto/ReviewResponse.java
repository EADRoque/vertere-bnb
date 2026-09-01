package com.vertere.reviewservice.review.dto;

import java.time.Instant;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        UUID listingId,
        UUID bookingId,
        UUID guestUserId,
        int rating,
        String comment,
        String hostResponse,
        Instant createdAt
) {}