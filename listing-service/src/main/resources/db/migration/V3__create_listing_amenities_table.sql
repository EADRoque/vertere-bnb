-- V3__create_listing_amenities_table.sql
-- Creates the "listing_amenities" join table, which links listings to the
-- amenities they offer (a listing can have many amenities, and an
-- amenity can belong to many listings).

CREATE TABLE listing_amenities (
    listing_id UUID NOT NULL REFERENCES listings(id),    -- points back to a row in the listings table
    amenity_id UUID NOT NULL REFERENCES amenities(id),   -- points to a row in the amenities table
    PRIMARY KEY (listing_id, amenity_id)                  -- each listing/amenity pair can only exist once
);
