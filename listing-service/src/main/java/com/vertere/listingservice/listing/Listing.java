package com.vertere.listingservice.listing;  //which folder/namespace this class belongs to

import java.math.BigDecimal;   //precise number type used for money (price, fees)
import java.time.Instant;   //represents a single point in time, used for createdAt
import java.util.UUID;   //the type used for this entity's unique id
import java.util.HashSet;   //the default, empty collection used to store amenities
import java.util.Set;   //the collection type used to hold this listing's amenities

import jakarta.persistence.JoinColumn;   //describes one side of the amenities join table
import jakarta.persistence.JoinTable;   //describes the shared table that links listings to amenities
import jakarta.persistence.ManyToMany;   //marks the amenities field as a many-to-many relationship
import jakarta.persistence.Column;   //maps a field to a specific database column
import jakarta.persistence.Entity;   //marks this class as a database table
import jakarta.persistence.GeneratedValue;   //tells JPA to auto-generate the id value
import jakarta.persistence.GenerationType;   //the strategy used to generate the id (UUID)
import jakarta.persistence.Id;   //marks the primary key field
import jakarta.persistence.Table;   //names the actual database table for this entity



/**
 * This class represents a single property listing (like an Airbnb
 * listing) as stored in the database - one row in the "listings" table.
 *
 * - id: a unique, auto-generated identifier for this listing.
 * - hostUserId: which user owns/created this listing.
 * - title, description, propertyType: the basic info shown to guests.
 * - city, country: where the listing is located.
 * - maxGuests: how many people can stay here.
 * - basePrice, cleaningFee: what it costs to book.
 * - cancellationPolicy: the rules if a guest cancels.
 * - active: whether this listing is currently visible/bookable.
 * - createdAt: when this listing was first created.
 * - amenities: the set of Amenity entities linked to this listing (e.g.
 *   Wifi, Pool) through a shared "listing_amenities" join table.
 * - protected Listing(): an empty constructor required by JPA/Hibernate
 *   so it can build objects from database rows behind the scenes.
 * - public Listing(...): the constructor actually used in code to build
 *   a brand new listing with all its required fields.
 */
@Entity   //tells Spring/JPA "this class maps to a database table"
@Table(name = "listings")   //the actual table name in the database
public class Listing {

    @Id   //marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.UUID)   //auto-generate a random UUID for each new listing
    private UUID id;

    @Column(name = "host_user_id", nullable = false)   //maps to the host_user_id column; can't be empty
    private UUID hostUserId;

    @Column(nullable = false)   //maps to a "title" column; can't be empty
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")   //stored as a large TEXT column since descriptions can be long
    private String description;

    @Column(name = "property_type", nullable = false)   //e.g. "apartment", "house"; can't be empty
    private String propertyType;

    @Column(nullable = false)   //can't be empty
    private String city;

    @Column(nullable = false)   //can't be empty
    private String country;

    @Column(name = "max_guests", nullable = false)   //how many guests this listing allows; can't be empty
    private int maxGuests;

    @Column(name = "base_price", nullable = false)   //nightly price; can't be empty
    private BigDecimal basePrice;

    @Column(name = "cleaning_fee", nullable = false)   //flat cleaning charge; can't be empty
    private BigDecimal cleaningFee;

    @Column(name = "cancellation_policy", nullable = false)   //e.g. "flexible", "strict"; can't be empty
    private String cancellationPolicy;

    @Column(nullable = false)   //whether the listing is visible/bookable
    private boolean active = true;   //new listings default to active

    @Column(name = "created_at", nullable = false, updatable = false)   //set once on creation, never changed afterward
    private Instant createdAt = Instant.now();   //stamped with the current time when the object is built

    @ManyToMany   //a listing can have many amenities, and an amenity can belong to many listings
    @JoinTable(
        name = "listing_amenities",   //the table in the middle that links listings and amenities
        joinColumns = @JoinColumn(name = "listing_id"),   //the column pointing back to this listing
        inverseJoinColumns = @JoinColumn(name = "amenity_id")   //the column pointing to the linked amenity
    )
    private Set<Amenity> amenities = new HashSet<>();   //starts empty; amenities are added later

    public Set<Amenity> getAmenities() {
        return amenities;
    }

    public void addAmenity(Amenity amenity) {
        amenities.add(amenity);   //links this listing to the given amenity
    }

    public void removeAmenity(Amenity amenity) {
        amenities.remove(amenity);   //unlinks this listing from the given amenity
    }

    protected Listing() {   //empty constructor required by JPA/Hibernate to build objects from database rows

    }

    public Listing(   //the constructor actually used in code to create a new listing
        UUID hostUserId,
        String title,
        String description,
        String propertyType,
        String city,
        String country,
        int maxGuests,
        BigDecimal basePrice,
        BigDecimal cleaningFee,
        String cancellationPolicy
    ) {
        this.hostUserId = hostUserId;
        this.title = title;
        this.description = description;
        this.propertyType = propertyType;
        this.city = city;
        this.country = country;
        this.maxGuests = maxGuests;
        this.basePrice = basePrice;
        this.cleaningFee = cleaningFee;
        this.cancellationPolicy = cancellationPolicy;
    }

     public UUID getId() {
        return id;
    }

    public UUID getHostUserId() {
        return hostUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getCleaningFee() {
        return cleaningFee;
    }

    public void setCleaningFee(BigDecimal cleaningFee) {
        this.cleaningFee = cleaningFee;
    }

    public String getCancellationPolicy() {
        return cancellationPolicy;
    }

    public void setCancellationPolicy(String cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    
}
