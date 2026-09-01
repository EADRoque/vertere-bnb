package com.vertere.bookingservice.booking;  //which folder/namespace this class belongs to

import jakarta.persistence.*;   //JPA annotations used to map this class to a database table
import java.math.BigDecimal;   //precise number type used for money (totalAmount)
import java.time.Instant;   //represents a single point in time, used for createdAt/cancelledAt
import java.time.LocalDate;   //represents a calendar date with no time component
import java.util.UUID;   //the type used for this entity's, listing's, and guest's id

/**
 * This class represents a single reservation made by a guest for a
 * listing over a date range - one row in the "bookings" table. The
 * database itself also enforces that a listing can't have two
 * overlapping active bookings (see the no_overlapping_bookings
 * constraint in the migration).
 *
 * - id: a unique, auto-generated identifier for this booking.
 * - listingId: which listing is being booked.
 * - guestUserId: which user made the booking.
 * - checkIn, checkOut: the date range of the stay.
 * - status: the booking's current state (starts as "PENDING").
 * - totalAmount, currency: what the guest owes for this stay.
 * - createdAt: when this booking was first created.
 * - cancelledAt: when this booking was cancelled, if it was.
 * - protected Booking(): an empty constructor required by JPA/Hibernate
 *   so it can build objects from database rows behind the scenes.
 * - public Booking(...): the constructor actually used in code to create
 *   a brand new booking.
 */
@Entity   //tells Spring/JPA "this class maps to a database table"
@Table(name = "bookings")   //the actual table name in the database
public class Booking {

    @Id   //marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.UUID)   //auto-generate a random UUID for each new booking
    private UUID id;

    @Column(name = "listing_id", nullable = false)   //which listing this booking is for; can't be empty
    private UUID listingId;

    @Column(name = "guest_user_id", nullable = false)   //which user made this booking; can't be empty
    private UUID guestUserId;

    @Column(name = "check_in", nullable = false)   //start of the stay; can't be empty
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false)   //end of the stay; can't be empty
    private LocalDate checkOut;

    @Column(nullable = false)   //e.g. "PENDING", "CONFIRMED", "CANCELLED"
    private String status;

    @Column(name = "total_amount", nullable = false)   //what the guest owes; can't be empty
    private BigDecimal totalAmount;

    @Column(nullable = false)   //defaults to USD unless set otherwise
    private String currency = "USD";

    @Column(name = "created_at", nullable = false, updatable = false)   //set once on creation, never changed afterward
    private Instant createdAt = Instant.now();   //stamped with the current time when the object is built

    @Column(name = "cancelled_at")   //stays empty unless the booking is later cancelled
    private Instant cancelledAt;

    protected Booking() {   //empty constructor required by JPA/Hibernate to build objects from database rows
    }

    public Booking(UUID listingId, UUID guestUserId, LocalDate checkIn, LocalDate checkOut, BigDecimal totalAmount) {   //the constructor actually used in code to create a new booking
        this.listingId = listingId;
        this.guestUserId = guestUserId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = totalAmount;
        this.status = "PENDING";   //every new booking starts out pending
    }

    public UUID getId() {
        return id;
    }

    public UUID getListingId() {
        return listingId;
    }

    public UUID getGuestUserId() {
        return guestUserId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

}