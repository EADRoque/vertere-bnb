-- V5__create_price_overrides_table.sql
-- Creates the "price_overrides" table, which stores custom nightly prices
-- for specific dates (e.g. holiday pricing) that override a listing's
-- normal base_price.

CREATE TABLE price_overrides (
    id UUID PRIMARY KEY,                                  -- unique identifier for this price override entry
    listing_id UUID NOT NULL REFERENCES listings(id),     -- which listing this override applies to
    override_date DATE NOT NULL,                           -- the specific date this custom price applies to
    price NUMERIC(10,2) NOT NULL,                           -- the custom price to use on that date
    UNIQUE (listing_id, override_date)                      -- a listing can only have one override per date
);
