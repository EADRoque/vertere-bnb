package com.vertere.messagingservice.messaging;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vertere.messagingservice.messaging.dto.ConversationResponse;
import com.vertere.messagingservice.messaging.dto.MessageResponse;
import com.vertere.messagingservice.messaging.dto.SendMessageRequest;
import com.vertere.messagingservice.messaging.dto.StartConversationRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/conversations")
public class MessagingController {

    private final MessagingService messagingService;

    public MessagingController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @PostMapping
    public ResponseEntity<ConversationResponse> start(
            Authentication authentication,
            @Valid @RequestBody StartConversationRequest request
    ) {
        UUID guestUserId = UUID.fromString(authentication.getName());
        ConversationResponse response = messagingService.startConversation(guestUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable UUID id,
            Authentication authentication,
            @Valid @RequestBody SendMessageRequest request
    ) {
        UUID senderUserId = UUID.fromString(authentication.getName());
        MessageResponse response = messagingService.sendMessage(id, senderUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ConversationResponse>> getMine(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(messagingService.getMyConversations(userId));
    }

}