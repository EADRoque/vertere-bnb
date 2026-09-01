package com.vertere.listingservice.listing.exception;  //which folder/namespace this class belongs to

/**
 * This exception is thrown when a user tries to modify a listing (update,
 * deactivate, block a date, set a price override) that they don't own.
 * Caught by ListingExceptionHandler and turned into a 403 response.
 *
 * - message: the error text passed up to the caller, shown in the
 *   response body.
 */
public class NotListingOwnerException extends RuntimeException {   //a runtime exception since callers aren't forced to catch it
    public NotListingOwnerException(String message) {
        super(message);   //hands the message off to the base Exception class
    }
}
