-- V1__create_conversations_and_messages_tables.sql
-- Creates the "conversations" table (one row per guest/host thread about
-- a listing) and the "messages" table (one row per message sent within
-- a conversation).

CREATE TABLE conversations (
    id UUID PRIMARY KEY,                     -- unique identifier for this conversation
    listing_id UUID NOT NULL,                 -- which listing this conversation is about
    guest_user_id UUID NOT NULL,               -- one of the two participants
    host_user_id UUID NOT NULL,                 -- the other participant
    created_at TIMESTAMPTZ NOT NULL              -- when this conversation was first started
);

CREATE TABLE messages (
    id UUID PRIMARY KEY,                                        -- unique identifier for this message
    conversation_id UUID NOT NULL REFERENCES conversations(id),  -- which conversation this message belongs to
    sender_user_id UUID NOT NULL,                                 -- who sent this message
    body TEXT NOT NULL,                                            -- the message text
    sent_at TIMESTAMPTZ NOT NULL,                                   -- when this message was sent
    read_at TIMESTAMPTZ                                              -- when the recipient read it, if they have yet
);
