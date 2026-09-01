package com.vertere.listingservice.listing.exception;  //which folder/namespace this class belongs to

/**
 * This exception is thrown when code looks up a listing by id and no
 * such listing exists. Caught by ListingExceptionHandler and turned
 * into a 404 response.
 *
 * - message: the error text passed up to the caller, shown in the
 *   response body.
 */
public class ListingNotFoundException extends RuntimeException {   //a runtime exception since callers aren't forced to catch it
    public ListingNotFoundException(String message) {
        super(message);   //hands the message off to the base Exception class
    }
}
