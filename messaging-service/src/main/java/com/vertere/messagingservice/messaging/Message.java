package com.vertere.messagingservice.messaging;  //which folder/namespace this class belongs to

import jakarta.persistence.*;   //JPA annotations used to map this class to a database table
import java.time.Instant;   //represents a single point in time, used for sentAt/readAt
import java.util.UUID;   //the type used for this entity's, conversation's, and sender's id

/**
 * This class represents a single message within a conversation - one
 * row in the "messages" table.
 *
 * - id: a unique, auto-generated identifier for this message.
 * - conversationId: which conversation this message belongs to.
 * - senderUserId: who sent this message (either the guest or the host
 *   from the parent Conversation).
 * - body: the actual text of the message.
 * - sentAt: when this message was sent.
 * - readAt: when the recipient read this message, if they have yet.
 * - protected Message(): an empty constructor required by JPA/Hibernate
 *   so it can build objects from database rows behind the scenes.
 * - public Message(...): the constructor actually used in code to send
 *   a new message.
 */
@Entity   //tells Spring/JPA "this class maps to a database table"
@Table(name = "messages")   //the actual table name in the database
public class Message {

    @Id   //marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.UUID)   //auto-generate a random UUID for each new message
    private UUID id;

    @Column(name = "conversation_id", nullable = false)   //which conversation this message belongs to; can't be empty
    private UUID conversationId;

    @Column(name = "sender_user_id", nullable = false)   //who sent this message; can't be empty
    private UUID senderUserId;

    @Column(nullable = false)   //the message text; can't be empty
    private String body;

    @Column(name = "sent_at", nullable = false, updatable = false)   //set once on creation, never changed afterward
    private Instant sentAt = Instant.now();   //stamped with the current time when the object is built

    @Column(name = "read_at")   //stays empty until the recipient reads the message
    private Instant readAt;

    protected Message() {   //empty constructor required by JPA/Hibernate to build objects from database rows
    }

    public Message(UUID conversationId, UUID senderUserId, String body) {   //the constructor actually used in code to send a new message
        this.conversationId = conversationId;
        this.senderUserId = senderUserId;
        this.body = body;
    }

    public UUID getId() {
        return id;
    }

    public UUID getConversationId() {
        return conversationId;
    }

    public UUID getSenderUserId() {
        return senderUserId;
    }

    public String getBody() {
        return body;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
    }

}