package com.vertere.messagingservice.messaging.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ConversationResponse(
        UUID id,
        UUID listingId,
        UUID guestUserId,
        UUID hostUserId,
        Instant createdAt,
        List<MessageResponse> messages
) {}