-- V2__create_amenities_table.sql
-- Creates the "amenities" table, which stores the reusable list of
-- amenities (like "Wifi" or "Pool") that listings can offer.

CREATE TABLE amenities (
    id UUID PRIMARY KEY,                     -- unique identifier for this amenity
    name VARCHAR(255) NOT NULL UNIQUE        -- the amenity's display name; no duplicates allowed
);
