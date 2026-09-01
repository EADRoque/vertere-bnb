CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    listing_id UUID NOT NULL,
    booking_id UUID NOT NULL,
    guest_user_id UUID NOT NULL,
    rating INT NOT NULL,
    comment TEXT NOT NULL,
    host_response TEXT,
    created_at TIMESTAMPTZ NOT NULL
);