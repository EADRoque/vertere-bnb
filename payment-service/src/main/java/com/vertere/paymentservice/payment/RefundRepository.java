package com.vertere.paymentservice.payment;  //which folder/namespace this class belongs to

import java.util.UUID;   //the type used for Refund's id

import org.springframework.data.jpa.repository.JpaRepository;   //gives us free save/find/delete database methods

/**
 * This interface handles reading/writing Refund rows in the database.
 * Spring automatically generates the implementation - we just declare
 * what we need.
 *
 * - JpaRepository<Refund, UUID>: gives this interface all the standard
 *   database operations (save, findById, findAll, delete, etc.) for
 *   free. No custom queries are needed yet, so the body is empty.
 */
public interface RefundRepository extends JpaRepository<Refund, UUID> {
}