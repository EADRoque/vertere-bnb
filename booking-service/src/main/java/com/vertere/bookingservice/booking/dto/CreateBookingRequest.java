package com.vertere.bookingservice.booking.dto;  //which folder/namespace this class belongs to

import java.time.LocalDate;   //represents a calendar date with no time component
import java.util.UUID;   //the type used for the listing's id

import jakarta.validation.constraints.NotNull;   //rejects the request if the field is missing entirely

/**
 * This is the shape of the data a client must send to create a new
 * booking. Spring validates every field automatically before the
 * controller method even runs.
 *
 * - listingId: which listing to book; required.
 * - checkIn, checkOut: the requested date range; both required.
 */
public record CreateBookingRequest(

    @NotNull   //must be present
    UUID listingId,

    @NotNull   //must be present
    LocalDate checkIn,

    @NotNull   //must be present
    LocalDate checkOut
) {}
