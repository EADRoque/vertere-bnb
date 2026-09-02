package com.vertere.messagingservice.messaging.dto;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID senderUserId,
        String body,
        Instant sentAt,
        Instant readAt
) {}