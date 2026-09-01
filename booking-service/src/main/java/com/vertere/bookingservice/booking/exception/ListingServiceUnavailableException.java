package com.vertere.bookingservice.booking.exception;  //which folder/namespace this class belongs to

/**
 * This exception is thrown when booking-service can't reach
 * listing-service (or listing-service errors out) while trying to check
 * a listing's price or blocked dates. Caught by BookingExceptionHandler
 * and turned into a 503 response, since the problem is a dependency
 * being unavailable, not the guest's request being invalid.
 *
 * - message: the error text passed up to the caller, shown in the
 *   response body.
 */
public class ListingServiceUnavailableException extends RuntimeException {   //a runtime exception since callers aren't forced to catch it
    public ListingServiceUnavailableException(String message) {
        super(message);   //hands the message off to the base Exception class
    }
}
