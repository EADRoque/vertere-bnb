package com.vertere.listingservice.listing;  //which folder/namespace this class belongs to

import java.util.Optional;   //wraps a result that might not exist, instead of returning null
import java.util.UUID;   //the type used for Amenity's id

import org.springframework.data.jpa.repository.JpaRepository;   //gives us free save/find/delete database methods

/**
 * This interface handles reading/writing Amenity rows in the database.
 * Spring automatically generates the implementation - we just declare
 * what queries we need.
 *
 * - JpaRepository<Amenity, UUID>: gives this interface all the standard
 *   database operations (save, findById, findAll, delete, etc.) for free.
 * - findByName: looks up an amenity by its exact name, if one exists.
 */
public interface AmenityRepository extends JpaRepository<Amenity, UUID> {
    Optional<Amenity> findByName(String name);   //returns empty if no amenity has this name
}
