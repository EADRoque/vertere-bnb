package com.vertere.bookingservice.booking.exception;  //which folder/namespace this class belongs to

/**
 * This exception is thrown when a guest tries to book dates that aren't
 * available - either because the host blocked them, or because another
 * booking already claims that date range for the same listing. Caught
 * by BookingExceptionHandler and turned into a 409 response.
 *
 * - message: the error text passed up to the caller, shown in the
 *   response body.
 */
public class DatesUnavailableException extends RuntimeException {   //a runtime exception since callers aren't forced to catch it
    public DatesUnavailableException(String message) {
        super(message);   //hands the message off to the base Exception class
    }
}
