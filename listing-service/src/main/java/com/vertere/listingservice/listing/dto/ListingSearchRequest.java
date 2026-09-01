package com.vertere.listingservice.listing.dto;  //which folder/namespace this class belongs to

import java.math.BigDecimal;   //precise number type used for money (price)
import java.time.LocalDate;   //represents a calendar date with no time component
import java.util.List;   //the collection type used to hold amenity names

/**
 * This is the shape of the search filters a client can send when
 * browsing listings. Every field is optional - only the filters that
 * are actually provided get applied (see ListingSpecifications).
 *
 * - city: only show listings in this city.
 * - checkIn, checkOut: the date range the guest wants to stay (not yet
 *   wired into filtering here, but reserved for availability checks).
 * - minGuests: only show listings that can fit at least this many guests.
 * - minPrice, maxPrice: only show listings within this price range.
 * - amenityNames: only show listings offering these amenities (not yet
 *   wired into filtering here, but reserved for future use).
 */
public record ListingSearchRequest(
        String city,   //optional; no city filter if omitted
        LocalDate checkIn,   //optional
        LocalDate checkOut,   //optional
        Integer minGuests,   //optional; no minimum if omitted
        BigDecimal minPrice,   //optional; no lower bound if omitted
        BigDecimal maxPrice,   //optional; no upper bound if omitted
        List<String> amenityNames   //optional
) {}
