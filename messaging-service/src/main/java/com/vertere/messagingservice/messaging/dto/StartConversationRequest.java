package com.vertere.messagingservice.messaging.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StartConversationRequest(
        @NotNull
        UUID listingId,

        @NotNull
        UUID hostUserId,

        @NotBlank
        String firstMessage
) {}