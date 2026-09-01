package com.vertere.listingservice.listing;  //which folder/namespace this class belongs to

import jakarta.persistence.*;   //JPA annotations used to map this class to a database table
import java.math.BigDecimal;   //precise number type used for money (price)
import java.time.LocalDate;   //represents a calendar date with no time component
import java.util.UUID;   //the type used for this entity's unique id

/**
 * This class represents a custom nightly price for a specific date (e.g.
 * holiday pricing) that overrides a listing's normal base price - one
 * row in the "price_overrides" table.
 *
 * - id: a unique, auto-generated identifier for this price override entry.
 * - listingId: which listing this override applies to.
 * - overrideDate: the specific date this custom price applies to.
 * - price: the custom price to use on that date.
 * - protected PriceOverride(): an empty constructor required by
 *   JPA/Hibernate so it can build objects from database rows behind the
 *   scenes.
 * - public PriceOverride(...): the constructor actually used in code to
 *   set a custom price for a listing on a given date.
 */
@Entity   //tells Spring/JPA "this class maps to a database table"
@Table(name = "price_overrides")   //the actual table name in the database
public class PriceOverride {

    @Id   //marks this field as the primary key
    @GeneratedValue(strategy= GenerationType.UUID)   //auto-generate a random UUID for each new entry
    private UUID id;

    @Column(name = "listing_id", nullable = false)   //which listing this override applies to; can't be empty
    private UUID listingId;

    @Column(name = "override_date", nullable = false)   //the date the custom price applies to; can't be empty
    private LocalDate overrideDate;

    @Column(nullable = false)   //the custom price; can't be empty
    private BigDecimal price;

    protected PriceOverride() {   //empty constructor required by JPA/Hibernate to build objects from database rows
    }

    public PriceOverride(UUID listingId, LocalDate overrideDate, BigDecimal price) {   //the constructor actually used in code
        this.listingId = listingId;
        this.overrideDate = overrideDate;
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public UUID getListingId() {
        return listingId;
    }

    public LocalDate getOverrideDate() {
        return overrideDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

}
