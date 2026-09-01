-- V4__create_blocked_dates_table.sql
-- Creates the "blocked_dates" table, which stores individual dates a host
-- has manually blocked off (marked unavailable) for a specific listing.

CREATE TABLE blocked_dates (
    id UUID PRIMARY KEY,                                  -- unique identifier for this blocked date entry
    listing_id UUID NOT NULL REFERENCES listings(id),     -- which listing this blocked date belongs to
    blocked_date DATE NOT NULL,                            -- the specific calendar date that's blocked
    UNIQUE (listing_id, blocked_date)                      -- a listing can only block a given date once
);
