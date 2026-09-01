package com.vertere.bookingservice.booking;  //which folder/namespace this class belongs to

import java.util.List;   //the collection type used to return multiple bookings
import java.util.UUID;   //the type used for Booking's, guest's, and listing's id

import org.springframework.data.jpa.repository.JpaRepository;   //gives us free save/find/delete database methods

/**
 * This interface handles reading/writing Booking rows in the database.
 * Spring automatically generates the implementation - we just declare
 * what queries we need.
 *
 * - JpaRepository<Booking, UUID>: gives this interface all the standard
 *   database operations (save, findById, findAll, delete, etc.) for free.
 * - findByGuestUserId: gets all bookings made by a specific guest (e.g.
 *   for a "my trips" page).
 * - findByListingId: gets all bookings made against a specific listing
 *   (e.g. for a host's dashboard).
 */
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByGuestUserId(UUID guestUserId);
    List<Booking> findByListingId(UUID listingId);
}
