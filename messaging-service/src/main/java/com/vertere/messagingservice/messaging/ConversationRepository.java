package com.vertere.messagingservice.messaging;  //which folder/namespace this class belongs to

import java.util.List;   //the collection type used to return multiple conversations
import java.util.Optional;   //wraps a result that might not exist, instead of returning null
import java.util.UUID;   //the type used for Conversation's, listing's, guest's, and host's id

import org.springframework.data.jpa.repository.JpaRepository;   //gives us free save/find/delete database methods

/**
 * This interface handles reading/writing Conversation rows in the
 * database. Spring automatically generates the implementation - we just
 * declare what queries we need.
 *
 * - JpaRepository<Conversation, UUID>: gives this interface all the
 *   standard database operations (save, findById, findAll, delete, etc.)
 *   for free.
 * - findByListingIdAndGuestUserId: looks up the existing conversation
 *   between a specific guest and a specific listing, if one already
 *   exists (so a new one isn't created every time they message).
 * - findByGuestUserIdOrHostUserId: gets every conversation a user is
 *   part of, whether they're the guest or the host side of it (e.g. for
 *   an inbox view).
 */
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    Optional<Conversation> findByListingIdAndGuestUserId(UUID listingId, UUID guestUserId);   //returns empty if this guest hasn't messaged about this listing yet
    List<Conversation> findByGuestUserIdOrHostUserId(UUID guestUserId, UUID hostUserId);   //note: pass the same user id for both params to get all of that user's conversations
}