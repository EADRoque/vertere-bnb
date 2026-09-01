package com.vertere.listingservice.listing.dto;  //which folder/namespace this class belongs to

import java.math.BigDecimal;   //precise number type used for money (price, fees)

import jakarta.validation.constraints.DecimalMin;   //rejects the request if the number is below the given minimum
import jakarta.validation.constraints.Min;   //rejects the request if the number is below the given minimum
import jakarta.validation.constraints.NotBlank;   //rejects the request if the text is missing or empty
import jakarta.validation.constraints.NotNull;   //rejects the request if the field is missing entirely

/**
 * This is the shape of the data a client must send to update an
 * existing listing. Unlike CreateListingRequest, it doesn't include
 * amenityNames - amenities are managed through separate endpoints.
 *
 * - title, description, propertyType, city, country: required text
 *   fields describing the listing.
 * - maxGuests: must be at least 1.
 * - basePrice, cleaningFee: required, and can't be negative.
 * - cancellationPolicy: required text field.
 */
public record UpdateListingRequest(

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
        String cancellationPolicy

) {}
