package com.vertere.paymentservice.payment;  //which folder/namespace this class belongs to

import jakarta.persistence.*;   //JPA annotations used to map this class to a database table
import java.math.BigDecimal;   //precise number type used for money (amount)
import java.time.Instant;   //represents a single point in time, used for createdAt
import java.util.UUID;   //the type used for this entity's and the booking's id

/**
 * This class represents a single attempt to charge a guest for a
 * booking - one row in the "payments" table. A booking can end up with
 * multiple payment attempts if earlier ones are declined.
 *
 * - id: a unique, auto-generated identifier for this payment.
 * - bookingId: which booking this payment is for.
 * - amount, currency: how much was charged, and in what currency.
 * - status: the outcome of the charge (e.g. "SUCCEEDED", "DECLINED").
 * - createdAt: when this payment attempt was made.
 * - protected Payment(): an empty constructor required by JPA/Hibernate
 *   so it can build objects from database rows behind the scenes.
 * - public Payment(...): the constructor actually used in code to record
 *   a new payment attempt.
 */
@Entity   //tells Spring/JPA "this class maps to a database table"
@Table(name = "payments")   //the actual table name in the database
public class Payment {

    @Id   //marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.UUID)   //auto-generate a random UUID for each new payment
    private UUID id;

    @Column(name = "booking_id", nullable = false)   //which booking this payment is for; can't be empty
    private UUID bookingId;

    @Column(nullable = false)   //can't be empty
    private BigDecimal amount;

    @Column(nullable = false, length = 3)   //e.g. "USD"; can't be empty
    private String currency;

    @Column(nullable = false)   //e.g. "SUCCEEDED", "DECLINED"; can't be empty
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)   //set once on creation, never changed afterward
    private Instant createdAt = Instant.now();   //stamped with the current time when the object is built

    protected Payment() {   //empty constructor required by JPA/Hibernate to build objects from database rows
    }

    public Payment(UUID bookingId, BigDecimal amount, String currency, String status) {   //the constructor actually used in code to record a new payment
        this.bookingId = bookingId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}