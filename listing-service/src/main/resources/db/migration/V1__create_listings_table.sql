-- V1__create_listings_table.sql
-- Creates the "listings" table, which stores one row per property listing
-- (like an Airbnb listing) - its details, pricing, and whether it's active.

CREATE TABLE listings (
    id UUID PRIMARY KEY,                                    -- unique identifier for this listing
    host_user_id UUID NOT NULL,                             -- which user owns/created this listing
    title VARCHAR(255) NOT NULL,                             -- short name shown to guests
    description TEXT NOT NULL,                               -- longer write-up of the listing
    property_type VARCHAR(255) NOT NULL,                      -- e.g. "apartment", "house"
    city VARCHAR(255) NOT NULL,                                -- where the listing is located
    country VARCHAR(255) NOT NULL,                             -- where the listing is located
    max_guests INT NOT NULL,                                   -- how many people can stay here
    base_price NUMERIC(10,2) NOT NULL,                         -- nightly price
    cleaning_fee NUMERIC(10,2) NOT NULL,                       -- flat cleaning charge
    cancellation_policy VARCHAR(255) NOT NULL,                 -- e.g. "flexible", "strict"
    active BOOLEAN NOT NULL DEFAULT TRUE,                      -- whether this listing is currently visible/bookable
    created_at TIMESTAMPTZ NOT NULL                            -- when this listing was first created
);
