package com.vertere.userservice.user.dto;  //which folder/namespace this class belongs to

import jakarta.validation.constraints.Email;   //checks the string looks like a real email address
import jakarta.validation.constraints.NotBlank;   //checks the string isn't null, empty, or just whitespace
import jakarta.validation.constraints.Size;   //checks the string's length is within given bounds

/**
 * This record describes the data we expect from the client when someone
 * signs up for a new account. It's not stored directly in the database -
 * it's just the shape of the incoming request, which we validate and then
 * use to build a real User.
 *
 * - email: must be present and look like a valid email address.
 * - password: must be present and at least 8 characters long. This is the
 *   plain password the user typed - it gets hashed before it's ever saved.
 * - fullName: must be present, no other restrictions.
 *
 * Being a record means it's just an immutable bundle of these three
 * values, with no extra behavior - Java generates the constructor,
 * getters, equals/hashCode, and toString for us.
 */
public record  RegisterRequest(

    @NotBlank   //can't be empty/blank
    @Email   //must look like a valid email address
    String email,

    @NotBlank   //can't be empty/blank
    @Size(min = 8, message = "Password must be at least 8 characters")   //enforces a minimum length, with a custom error message
    String password,

    @NotBlank   //can't be empty/blank
    String fullName
) {}
