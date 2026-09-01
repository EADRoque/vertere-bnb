package com.vertere.bookingservice.booking.dto;  //which folder/namespace this class belongs to

import java.math.BigDecimal;   //precise number type used for money (totalAmount)
import java.time.Instant;   //represents a single point in time, used for createdAt
import java.time.LocalDate;   //represents a calendar date with no time component
import java.util.UUID;   //the type used for id fields

/**
 * This is the safe, public-facing shape of a booking that gets sent
 * back to clients after it's created or looked up.
 *
 * - id, listingId, guestUserId: identifies the booking, the listing it's
 *   for, and who made it.
 * - checkIn, checkOut: the date range of the stay.
 * - status: the booking's current state.
 * - totalAmount, currency: what the guest owes for this stay.
 * - createdAt: when this booking was first created.
 */
public record BookingResponse(
        UUID id,   //the booking's unique identifier
        UUID listingId,   //which listing was booked
        UUID guestUserId,   //who made the booking
        LocalDate checkIn,   //start of the stay
        LocalDate checkOut,   //end of the stay
        String status,   //e.g. "PENDING", "CONFIRMED", "CANCELLED"
        BigDecimal totalAmount,   //what the guest owes
        String currency,   //e.g. "USD"
        Instant createdAt   //when this booking was first created
) {}
