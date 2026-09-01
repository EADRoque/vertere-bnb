package com.vertere.listingservice.listing.dto;  //which folder/namespace this class belongs to

import java.time.LocalDate;   //represents a calendar date with no time component

import jakarta.validation.constraints.NotNull;   //rejects the request if the field is missing entirely

/**
 * This is the shape of the data a client sends to block off a single
 * date on a listing's calendar (marking it unavailable).
 *
 * - date: the calendar date to block; required.
 */
public record BlockDateRequest(

        @NotNull   //must be present
        LocalDate date

) {}
