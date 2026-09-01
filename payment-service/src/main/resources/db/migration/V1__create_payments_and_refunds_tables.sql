-- V1__create_payments_and_refunds_tables.sql
-- Creates the "payments" table (one row per charge attempt against a
-- booking) and the "refunds" table (one row per refund issued against
-- a payment).

CREATE TABLE payments (
    id UUID PRIMARY KEY,                     -- unique identifier for this payment
    booking_id UUID NOT NULL,                 -- which booking this payment is for
    amount NUMERIC(10,2) NOT NULL,             -- how much was charged
    currency VARCHAR(3) NOT NULL,               -- e.g. "USD"
    status VARCHAR(20) NOT NULL,                 -- e.g. "SUCCEEDED", "DECLINED"
    created_at TIMESTAMPTZ NOT NULL               -- when this payment attempt was made
);

CREATE TABLE refunds (
    id UUID PRIMARY KEY,                                  -- unique identifier for this refund
    payment_id UUID NOT NULL REFERENCES payments(id),      -- which payment this refund is against
    amount NUMERIC(10,2) NOT NULL,                          -- how much was refunded
    status VARCHAR(20) NOT NULL,                             -- e.g. "SUCCEEDED", "FAILED"
    created_at TIMESTAMPTZ NOT NULL                           -- when this refund was created
);
