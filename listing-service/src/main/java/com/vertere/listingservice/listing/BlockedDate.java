package com.vertere.listingservice.listing;  //which folder/namespace this class belongs to

import java.time.LocalDate;   //represents a calendar date with no time component
import java.util.UUID;   //the type used for this entity's unique id

import jakarta.persistence.Column;   //maps a field to a specific database column
import jakarta.persistence.Entity;   //marks this class as a database table
import jakarta.persistence.GeneratedValue;   //tells JPA to auto-generate the id value
import jakarta.persistence.GenerationType;   //the strategy used to generate the id (UUID)
import jakarta.persistence.Id;   //marks the primary key field
import jakarta.persistence.Table;   //names the actual database table for this entity

/**
 * This class represents a single date that a host has manually blocked
 * off (marked unavailable) for a listing - one row in the
 * "blocked_dates" table.
 *
 * - id: a unique, auto-generated identifier for this blocked date entry.
 * - listingId: which listing this blocked date belongs to.
 * - blockedDate: the specific calendar date that's blocked.
 * - protected BlockedDate(): an empty constructor required by
 *   JPA/Hibernate so it can build objects from database rows behind the
 *   scenes.
 * - public BlockedDate(...): the constructor actually used in code to
 *   block off a new date for a listing.
 */
@Entity   //tells Spring/JPA "this class maps to a database table"
@Table(name = "blocked_dates")   //the actual table name in the database
public class BlockedDate {

    @Id   //marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.UUID)   //auto-generate a random UUID for each new entry
    private UUID id;

    @Column(name = "listing_id", nullable = false)   //which listing this blocked date belongs to; can't be empty
    private UUID listingId;

    @Column(name = "blocked_date", nullable = false)   //the specific date that's blocked; can't be empty
    private LocalDate blockedDate;

    protected BlockedDate() {   //empty constructor required by JPA/Hibernate to build objects from database rows
    }

    public BlockedDate(UUID listingId, LocalDate blockedDate) {   //the constructor actually used in code
        this.listingId = listingId;
        this.blockedDate = blockedDate;
    }

    public UUID getId() {
        return id;
    }

    public UUID getListingId() {
        return listingId;
    }

    public LocalDate getBlockedDate() {
        return blockedDate;
    }
}
