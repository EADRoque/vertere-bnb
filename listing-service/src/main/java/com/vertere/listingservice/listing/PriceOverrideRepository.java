package com.vertere.listingservice.listing;  //which folder/namespace this class belongs to

import java.time.LocalDate;   //represents a calendar date with no time component
import java.util.List;   //the collection type used to return multiple price overrides
import java.util.UUID;   //the type used for PriceOverride's and Listing's id

import org.springframework.data.jpa.repository.JpaRepository;   //gives us free save/find/delete database methods

/**
 * This interface handles reading/writing PriceOverride rows in the
 * database. Spring automatically generates the implementation - we just
 * declare what queries we need.
 *
 * - JpaRepository<PriceOverride, UUID>: gives this interface all the
 *   standard database operations (save, findById, findAll, delete, etc.)
 *   for free.
 * - findByListingIdAndOverrideDateBetween: gets all custom prices set for
 *   a listing that fall within a given date range.
 */
public interface PriceOverrideRepository extends JpaRepository<PriceOverride, UUID> {
    List<PriceOverride> findByListingIdAndOverrideDateBetween(UUID listingId, LocalDate start, LocalDate end);   //e.g. for pricing out a stay across a date range
}
