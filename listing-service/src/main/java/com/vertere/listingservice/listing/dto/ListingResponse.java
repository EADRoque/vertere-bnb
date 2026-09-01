package com.vertere.listingservice.listing.dto;  //which folder/namespace this class belongs to

import java.math.BigDecimal;   //precise number type used for money (price, fees)
import java.time.Instant;   //represents a single point in time, used for createdAt
import java.util.List;   //the collection type used to hold the amenity names
import java.util.UUID;   //the type used for id fields

/**
 * This is the safe, public-facing shape of a listing that gets sent back
 * to clients - it leaves out any internal-only fields and flattens the
 * amenities down to just their names.
 *
 * - id, hostUserId: identifies the listing and who owns it.
 * - title, description, propertyType, city, country: the basic info
 *   shown to guests.
 * - maxGuests, basePrice, cleaningFee, cancellationPolicy: booking details.
 * - active: whether this listing is currently visible/bookable.
 * - createdAt: when this listing was first created.
 * - amenities: the list of amenity names this listing offers.
 */
public record ListingResponse(
        UUID id,   //the listing's unique identifier
        UUID hostUserId,   //which user owns this listing
        String title,   //short name shown to guests
        String description,   //longer write-up of the listing
        String propertyType,   //e.g. "apartment", "house"
        String city,   //where the listing is located
        String country,   //where the listing is located
        int maxGuests,   //how many people can stay here
        BigDecimal basePrice,   //nightly price
        BigDecimal cleaningFee,   //flat cleaning charge
        String cancellationPolicy,   //e.g. "flexible", "strict"
        boolean active,   //whether this listing is currently visible/bookable
        Instant createdAt,   //when this listing was first created
        List<String> amenities   //names of the amenities this listing offers
) {}
