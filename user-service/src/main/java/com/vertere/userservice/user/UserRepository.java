package com.vertere.userservice.user;  //which folder/namespace this class belongs to

import java.util.Optional;   //a wrapper that can hold "a value" or "nothing", used so callers must handle the not-found case
import java.util.UUID;   //represents a universally unique ID, used for User's id field

import org.springframework.data.jpa.repository.JpaRepository;   //Spring Data base interface that already gives us save/find/delete etc.

/**
 * This interface is how the rest of the app talks to the "users" table
 * without writing any SQL by hand.
 *
 * - JpaRepository<User, UUID>: tells Spring Data this repository works
 *   with User entities, and that a User's id is a UUID. Just by extending
 *   this, we automatically get methods like save(), findById(), findAll(),
 *   and delete() for free.
 * - findByEmail / existsByEmail: custom lookups we added ourselves. Spring
 *   Data reads the method name ("find by email", "exists by email") and
 *   generates the matching database query automatically - we never
 *   implement either method's body.
 * - Optional<User>: since a given email might not match any user, this
 *   forces callers to explicitly handle the "no user found" case instead
 *   of risking a null pointer.
 *
 * There's no class body/implementation here because Spring Data creates
 * the real implementation behind the scenes when the app starts up.
 */
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);   //Spring Data auto-generates: SELECT * FROM users WHERE email = ?
    boolean existsByEmail(String email);   //Spring Data auto-generates: SELECT COUNT(*) > 0 FROM users WHERE email = ?, no need to load the whole row
}
