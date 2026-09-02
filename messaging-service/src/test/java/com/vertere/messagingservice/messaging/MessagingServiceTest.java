package com.vertere.messagingservice.messaging;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vertere.messagingservice.messaging.dto.ConversationResponse;
import com.vertere.messagingservice.messaging.dto.SendMessageRequest;
import com.vertere.messagingservice.messaging.dto.StartConversationRequest;
import com.vertere.messagingservice.messaging.exception.ConversationNotFoundException;

@ExtendWith(MockitoExtension.class)
class MessagingServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessagingService messagingService;

    private UUID guestId;
    private UUID hostId;
    private UUID listingId;

    @BeforeEach
    void setUp() {
        guestId = UUID.randomUUID();
        hostId = UUID.randomUUID();
        listingId = UUID.randomUUID();
    }

    @Test
    void startConversation_createsNewConversation_whenNoneExists() {
        StartConversationRequest request = new StartConversationRequest(listingId, hostId, "Hello!");

        when(conversationRepository.findByListingIdAndGuestUserId(listingId, guestId)).thenReturn(Optional.empty());
        when(conversationRepository.save(any(Conversation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(messageRepository.findByConversationIdOrderBySentAtAsc(any())).thenReturn(List.of());

        ConversationResponse response = messagingService.startConversation(guestId, request);

        assertEquals(listingId, response.listingId());
        assertEquals(guestId, response.guestUserId());
        verify(conversationRepository).save(any(Conversation.class));
    }

    @Test
    void startConversation_reusesExistingConversation_whenOneAlreadyExists() {
        Conversation existing = new Conversation(listingId, guestId, hostId);
        StartConversationRequest request = new StartConversationRequest(listingId, hostId, "Hello again!");

        when(conversationRepository.findByListingIdAndGuestUserId(listingId, guestId)).thenReturn(Optional.of(existing));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(messageRepository.findByConversationIdOrderBySentAtAsc(any())).thenReturn(List.of());

        messagingService.startConversation(guestId, request);

        verify(conversationRepository, never()).save(any(Conversation.class));
    }

    @Test
    void sendMessage_throwsException_whenConversationNotFound() {
        UUID conversationId = UUID.randomUUID();
        SendMessageRequest request = new SendMessageRequest("Hello?");

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.empty());

        assertThrows(ConversationNotFoundException.class,
                () -> messagingService.sendMessage(conversationId, guestId, request));
    }

}