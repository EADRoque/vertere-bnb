package com.vertere.listingservice.listing.dto;  //which folder/namespace this class belongs to

import java.time.LocalDate;   //represents a calendar date with no time component
import java.util.List;   //the collection type used to hold blocked dates
import java.util.Map;   //pairs each overridden date with its custom price

/**
 * This is the shape of the data sent back when a client asks for a
 * listing's availability over a date range.
 *
 * - blockedDates: dates the host has manually marked unavailable.
 * - priceOverrides: dates with a custom price, mapped to that price.
 */
public record AvailabilityResponse(
        List<LocalDate> blockedDates,   //dates that are unavailable
        Map<LocalDate, java.math.BigDecimal> priceOverrides   //custom price per date, if any
) {}
