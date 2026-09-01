-- V1__create_bookings_table.sql
-- Creates the "bookings" table, which stores one row per reservation a
-- guest makes for a listing, and adds a database-level rule that stops
-- two active bookings from overlapping on the same listing.

CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE bookings (
    id UUID PRIMARY KEY,                                -- unique identifier for this booking
    listing_id UUID NOT NULL,                            -- which listing is being booked
    guest_user_id UUID NOT NULL,                          -- who made the booking
    check_in DATE NOT NULL,                                -- start of the stay
    check_out DATE NOT NULL,                                -- end of the stay
    stay_range DATERANGE GENERATED ALWAYS AS (daterange(check_in, check_out, '[)')) STORED,   -- auto-computed date range (check_in included, check_out excluded), used below to detect overlaps
    status VARCHAR(20) NOT NULL,                              -- e.g. "PENDING", "CONFIRMED", "CANCELLED"
    total_amount NUMERIC(10,2) NOT NULL,                       -- what the guest owes for this stay
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',                 -- defaults to USD unless set otherwise
    created_at TIMESTAMPTZ NOT NULL,                             -- when this booking was first created
    cancelled_at TIMESTAMPTZ                                      -- when this booking was cancelled, if it was
);

-- Stops two PENDING/CONFIRMED bookings for the same listing from ever
-- having overlapping stay_ranges - the database rejects the second save
-- outright, so this is the final safety net against double-booking even
-- if two requests race each other at the exact same time.
ALTER TABLE bookings
    ADD CONSTRAINT no_overlapping_bookings
    EXCLUDE USING gist (
        listing_id WITH =,     -- only compare bookings for the same listing
        stay_range WITH &&     -- ...and reject it if their date ranges overlap
    )
    WHERE (status IN ('PENDING', 'CONFIRMED'));   -- cancelled bookings don't block new ones from reusing those dates
