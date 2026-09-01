package com.vertere.listingservice.listing;  //which folder/namespace this class belongs to

import java.util.UUID;   //the type used for Listing's id

import org.springframework.data.jpa.repository.JpaRepository;   //gives us free save/find/delete database methods
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;   //lets us build flexible, dynamic search filters (e.g. by city, price range) at runtime

/**
 * This interface handles reading/writing Listing rows in the database.
 * Spring automatically generates the implementation - we just declare
 * what capabilities we need.
 *
 * - JpaRepository<Listing, UUID>: gives this interface all the standard
 *   database operations (save, findById, findAll, delete, etc.) for free.
 * - JpaSpecificationExecutor<Listing>: lets callers build custom search
 *   queries (e.g. "listings in this city under this price") without
 *   writing a new method for every combination of filters.
 */
public interface ListingRepository extends JpaRepository<Listing, UUID>, JpaSpecificationExecutor<Listing> {

}
