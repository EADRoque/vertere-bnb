package com.vertere.messagingservice.messaging;  //which folder/namespace this class belongs to

import jakarta.persistence.*;   //JPA annotations used to map this class to a database table
import java.time.Instant;   //represents a single point in time, used for createdAt
import java.util.UUID;   //the type used for this entity's, listing's, guest's, and host's id

/**
 * This class represents a single conversation thread between a guest and
 * a host about a specific listing - one row in the "conversations"
 * table. The actual back-and-forth text lives in the Message entity,
 * which points back to a conversation.
 *
 * - id: a unique, auto-generated identifier for this conversation.
 * - listingId: which listing this conversation is about.
 * - guestUserId, hostUserId: the two participants in the conversation.
 * - createdAt: when this conversation was first started.
 * - protected Conversation(): an empty constructor required by
 *   JPA/Hibernate so it can build objects from database rows behind the
 *   scenes.
 * - public Conversation(...): the constructor actually used in code to
 *   start a new conversation.
 */
@Entity   //tells Spring/JPA "this class maps to a database table"
@Table(name = "conversations")   //the actual table name in the database
public class Conversation {

    @Id   //marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.UUID)   //auto-generate a random UUID for each new conversation
    private UUID id;

    @Column(name = "listing_id", nullable = false)   //which listing this conversation is about; can't be empty
    private UUID listingId;

    @Column(name = "guest_user_id", nullable = false)   //one of the two participants; can't be empty
    private UUID guestUserId;

    @Column(name = "host_user_id", nullable = false)   //the other participant; can't be empty
    private UUID hostUserId;

    @Column(name = "created_at", nullable = false, updatable = false)   //set once on creation, never changed afterward
    private Instant createdAt = Instant.now();   //stamped with the current time when the object is built

    protected Conversation() {   //empty constructor required by JPA/Hibernate to build objects from database rows
    }

    public Conversation(UUID listingId, UUID guestUserId, UUID hostUserId) {   //the constructor actually used in code to start a new conversation
        this.listingId = listingId;
        this.guestUserId = guestUserId;
        this.hostUserId = hostUserId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getListingId() {
        return listingId;
    }

    public UUID getGuestUserId() {
        return guestUserId;
    }

    public UUID getHostUserId() {
        return hostUserId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}