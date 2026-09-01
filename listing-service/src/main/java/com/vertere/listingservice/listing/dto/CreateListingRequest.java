package com.vertere.listingservice.listing.dto;  //which folder/namespace this class belongs to

import java.math.BigDecimal;   //precise number type used for money (price, fees)
import java.util.List;   //the collection type used to hold amenity names

import jakarta.validation.constraints.DecimalMin;   //rejects the request if the number is below the given minimum
import jakarta.validation.constraints.Min;   //rejects the request if the number is below the given minimum
import jakarta.validation.constraints.NotBlank;   //rejects the request if the text is missing or empty
import jakarta.validation.constraints.NotNull;   //rejects the request if the field is missing entirely

/**
 * This is the shape of the data a client must send to create a new
 * listing. Spring validates every field automatically before the
 * controller method even runs, based on the annotations below.
 *
 * - title, description, propertyType, city, country: required text
 *   fields describing the listing.
 * - maxGuests: must be at least 1.
 * - basePrice, cleaningFee: required, and can't be negative.
 * - cancellationPolicy: required text field.
 * - amenityNames: optional list of amenity names to attach to the
 *   listing (existing amenities are reused, new ones are created).
 */
public record CreateListingRequest(

        @NotBlank   //can't be missing or empty
        String title,

        @NotBlank   //can't be missing or empty
        String description,

        @NotBlank   //can't be missing or empty
        String propertyType,

        @NotBlank   //can't be missing or empty
        String city,

        @NotBlank   //can't be missing or empty
        String country,

        @Min(1)   //must allow at least 1 guest
        int maxGuests,

        @NotNull   //must be present
        @DecimalMin("0.0")   //can't be negative
        BigDecimal basePrice,

        @NotNull   //must be present
        @DecimalMin("0.0")   //can't be negative
        BigDecimal cleaningFee,

        @NotBlank   //can't be missing or empty
        String cancellationPolicy,

        List<String> amenityNames   //optional; no amenities are added if omitted

) {}
