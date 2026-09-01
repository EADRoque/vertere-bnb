package com.vertere.userservice.user.dto;  //which folder/namespace this class belongs to

import jakarta.validation.constraints.Email;   //checks the string looks like a real email address
import jakarta.validation.constraints.NotBlank;   //checks the string isn't null, empty, or just whitespace

/**
 * This record describes the data we expect from the client when someone
 * tries to log in. It's just the shape of the incoming request - we
 * validate it, then compare it against the stored User to decide whether
 * the login succeeds.
 *
 * - email: must be present and look like a valid email address.
 * - password: must be present. This is the plain password the user typed,
 *   which we check against the stored, hashed password - it's never
 *   saved anywhere itself.
 *
 * Being a record means it's just an immutable bundle of these two values,
 * with no extra behavior - Java generates the constructor, getters,
 * equals/hashCode, and toString for us.
 */
public record LoginRequest(

    @NotBlank   //can't be empty/blank
    @Email   //must look like a valid email address
    String email,

    @NotBlank   //can't be empty/blank
    String password

) {}
