package com.vertere.messagingservice.messaging;

import com.vertere.messagingservice.messaging.dto.*;
import com.vertere.messagingservice.messaging.exception.ConversationNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessagingService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public MessagingService(ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    public ConversationResponse startConversation(UUID guestUserId, StartConversationRequest request) {
        Conversation conversation = conversationRepository
                .findByListingIdAndGuestUserId(request.listingId(), guestUserId)
                .orElseGet(() -> conversationRepository.save(
                        new Conversation(request.listingId(), guestUserId, request.hostUserId())
                ));

        Message message = new Message(conversation.getId(), guestUserId, request.firstMessage());
        messageRepository.save(message);

        return toResponse(conversation);
    }

    public MessageResponse sendMessage(UUID conversationId, UUID senderUserId, SendMessageRequest request) {
        conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException("Conversation not found"));

        Message message = new Message(conversationId, senderUserId, request.body());
        Message saved = messageRepository.save(message);

        return toMessageResponse(saved);
    }

    public List<ConversationResponse> getMyConversations(UUID userId) {
        return conversationRepository.findByGuestUserIdOrHostUserId(userId, userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ConversationResponse toResponse(Conversation conversation) {
        List<MessageResponse> messages = messageRepository
                .findByConversationIdOrderBySentAtAsc(conversation.getId())
                .stream()
                .map(this::toMessageResponse)
                .collect(Collectors.toList());

        return new ConversationResponse(
                conversation.getId(),
                conversation.getListingId(),
                conversation.getGuestUserId(),
                conversation.getHostUserId(),
                conversation.getCreatedAt(),
                messages
        );
    }

    private MessageResponse toMessageResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getSenderUserId(),
                message.getBody(),
                message.getSentAt(),
                message.getReadAt()
        );
    }

}