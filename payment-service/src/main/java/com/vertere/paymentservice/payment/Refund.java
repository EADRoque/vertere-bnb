package com.vertere.paymentservice.payment;  //which folder/namespace this class belongs to

import java.math.BigDecimal;   //precise number type used for money (amount)
import java.time.Instant;   //represents a single point in time, used for createdAt
import java.util.UUID;   //the type used for this entity's and the payment's id

import jakarta.persistence.Column;   //maps a field to a specific database column
import jakarta.persistence.Entity;   //marks this class as a database table
import jakarta.persistence.GeneratedValue;   //tells JPA to auto-generate the id value
import jakarta.persistence.GenerationType;   //the strategy used to generate the id (UUID)
import jakarta.persistence.Id;   //marks the primary key field
import jakarta.persistence.Table;   //names the actual database table for this entity

/**
 * This class represents a refund issued against a previous payment - one
 * row in the "refunds" table.
 *
 * - id: a unique, auto-generated identifier for this refund.
 * - paymentId: which payment this refund is against.
 * - amount: how much was refunded.
 * - status: the outcome of the refund (e.g. "SUCCEEDED", "FAILED").
 * - createdAt: when this refund was created.
 * - protected Refund(): an empty constructor required by JPA/Hibernate
 *   so it can build objects from database rows behind the scenes.
 * - public Refund(...): the constructor actually used in code to record
 *   a new refund.
 */
@Entity   //tells Spring/JPA "this class maps to a database table"
@Table(name = "refunds")   //the actual table name in the database
public class Refund {

    @Id   //marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.UUID)   //auto-generate a random UUID for each new refund
    private UUID id;

    @Column(name = "payment_id", nullable = false)   //which payment this refund is against; can't be empty
    private UUID paymentId;

    @Column(nullable = false)   //can't be empty
    private BigDecimal amount;

    @Column(nullable = false)   //e.g. "SUCCEEDED", "FAILED"; can't be empty
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)   //set once on creation, never changed afterward
    private Instant createdAt = Instant.now();   //stamped with the current time when the object is built

    protected Refund() {   //empty constructor required by JPA/Hibernate to build objects from database rows
    }

    public Refund(UUID paymentId, BigDecimal amount, String status) {   //the constructor actually used in code to record a new refund
        this.paymentId = paymentId;
        this.amount = amount;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}