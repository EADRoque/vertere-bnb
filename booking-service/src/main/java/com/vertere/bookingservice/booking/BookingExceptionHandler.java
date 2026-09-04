package com.vertere.bookingservice.booking;  //which folder/namespace this class belongs to

import org.slf4j.Logger;   //logs the real error server-side for anything unexpected
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;   //the status code we translate the exception into
import org.springframework.http.ResponseEntity;   //wraps a response body together with its status code
import org.springframework.web.bind.annotation.ExceptionHandler;   //marks a method as handling a specific exception type
import org.springframework.web.bind.annotation.RestControllerAdvice;   //applies these handlers globally, across every controller

import com.vertere.bookingservice.booking.exception.BookingNotFoundException;
import com.vertere.bookingservice.booking.exception.DatesUnavailableException;   //thrown when the requested dates can't be booked
import com.vertere.bookingservice.booking.exception.ListingServiceUnavailableException;
import com.vertere.bookingservice.booking.exception.NotBookingOwnerException;

/**
 * This class catches specific exceptions thrown anywhere in the booking
 * service's request handling and turns them into proper HTTP error
 * responses, instead of a raw 500 error leaking internal details.
 *
 * - handleDatesUnavailable: turns a DatesUnavailableException into a 409
 *   Conflict response with the exception's message as the body.
 * - handleUnexpected: catches anything not anticipated above (a bug, a
 *   DB hiccup) - logs the real error server-side but returns a generic
 *   500 so internal details never leak to the client.
 */
@RestControllerAdvice   //applies to every controller in the app, no need to repeat this per-controller
public class BookingExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(BookingExceptionHandler.class);

    @ExceptionHandler(DatesUnavailableException.class)   //runs whenever this exception bubbles up from a controller
    public ResponseEntity<String> handleDatesUnavailable(DatesUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());   //409 - the request conflicts with existing bookings
    }

    @ExceptionHandler(ListingServiceUnavailableException.class)
    public ResponseEntity<String> handleListingServiceUnavailable(ListingServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<String> handleBookingNotFound(BookingNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(NotBookingOwnerException.class)
    public ResponseEntity<String> handleNotBookingOwner(NotBookingOwnerException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)   //catch-all - only reached when nothing more specific above matched
    public ResponseEntity<String> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong. Please try again.");
    }

}
