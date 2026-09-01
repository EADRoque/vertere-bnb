package com.vertere.userservice.user.dto;  //which folder/namespace this class belongs to

import java.time.Instant;   //represents a precise point in time, used for createdAt
import java.util.UUID;   //represents a universally unique ID, used for the id field

/**
 * This record describes what we send back to the client when they ask
 * about a user - for example after registering, logging in, or fetching
 * a profile. It mirrors User, but leaves out sensitive fields like
 * passwordHash so we never accidentally expose them over the API.
 *
 * - id: the user's unique ID.
 * - email: the user's email.
 * - fullName: the user's name.
 * - phone / avatarUrl: optional info, may be null.
 * - admin: true/false switch for whether this user has admin powers.
 * - createdAt: the date and time the account was created.
 *
 * Being a record means it's just an immutable bundle of these values,
 * with no extra behavior - Java generates the constructor, getters,
 * equals/hashCode, and toString for us.
 */
public record UserResponse(

    UUID id,   //the user's unique ID
    String email,   //the user's email
    String fullName,   //the user's name
    String phone,   //optional, may be null
    String avatarUrl,   //optional, may be null
    boolean admin,   //true/false switch for admin powers
    Instant createdAt   //when the account was created
) {}
