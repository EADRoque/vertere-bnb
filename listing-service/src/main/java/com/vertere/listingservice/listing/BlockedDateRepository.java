package com.vertere.listingservice.listing;  //which folder/namespace this class belongs to

import org.springframework.data.jpa.repository.JpaRepository;   //gives us free save/find/delete database methods
import java.time.LocalDate;   //represents a calendar date with no time component
import java.util.List;   //the collection type used to return multiple blocked dates
import java.util.UUID;   //the type used for BlockedDate's and Listing's id

/**
 * This interface handles reading/writing BlockedDate rows in the
 * database. Spring automatically generates the implementation - we just
 * declare what queries we need.
 *
 * - JpaRepository<BlockedDate, UUID>: gives this interface all the
 *   standard database operations (save, findById, findAll, delete, etc.)
 *   for free.
 * - findByListingIdAndBlockedDateBetween: gets all blocked dates for a
 *   listing that fall within a given date range.
 * - existsByListingIdAndBlockedDate: quickly checks whether a specific
 *   date is already blocked for a listing.
 */
public interface BlockedDateRepository extends JpaRepository<BlockedDate, UUID> {
    List<BlockedDate> findByListingIdAndBlockedDateBetween(UUID listingId, LocalDate start, LocalDate end);   //e.g. for showing a listing's calendar
    boolean existsByListingIdAndBlockedDate(UUID listingId, LocalDate blockedDate);   //e.g. for validating a new booking date
}
