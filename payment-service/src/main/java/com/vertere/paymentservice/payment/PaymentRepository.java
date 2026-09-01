package com.vertere.paymentservice.payment;  //which folder/namespace this class belongs to

import java.util.UUID;   //the type used for Payment's id

import org.springframework.data.jpa.repository.JpaRepository;   //gives us free save/find/delete database methods

/**
 * This interface handles reading/writing Payment rows in the database.
 * Spring automatically generates the implementation - we just declare
 * what we need.
 *
 * - JpaRepository<Payment, UUID>: gives this interface all the standard
 *   database operations (save, findById, findAll, delete, etc.) for
 *   free. No custom queries are needed yet, so the body is empty.
 */
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}