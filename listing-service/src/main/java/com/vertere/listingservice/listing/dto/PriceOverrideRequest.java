package com.vertere.listingservice.listing.dto;  //which folder/namespace this class belongs to

import java.math.BigDecimal;   //precise number type used for money (price)
import java.time.LocalDate;   //represents a calendar date with no time component

import jakarta.validation.constraints.DecimalMin;   //rejects the request if the number is below the given minimum
import jakarta.validation.constraints.NotNull;   //rejects the request if the field is missing entirely

/**
 * This is the shape of the data a client sends to set a custom price for
 * a specific date on a listing (e.g. holiday pricing).
 *
 * - date: the calendar date the custom price applies to; required.
 * - price: the custom price to use on that date; required, can't be
 *   negative.
 */
public record PriceOverrideRequest(

        @NotNull   //must be present
        LocalDate date,

        @NotNull   //must be present
        @DecimalMin("0.0")   //can't be negative
        BigDecimal price

) {}
