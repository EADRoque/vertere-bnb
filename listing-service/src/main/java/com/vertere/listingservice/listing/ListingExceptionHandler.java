package com.vertere.listingservice.listing;  //which folder/namespace this class belongs to

import com.vertere.listingservice.listing.exception.ListingNotFoundException;   //thrown when a listing id doesn't exist
import com.vertere.listingservice.listing.exception.NotListingOwnerException;   //thrown when a user tries to modify a listing they don't own
import org.springframework.http.HttpStatus;   //the status codes we translate each exception into
import org.springframework.http.ResponseEntity;   //wraps a response body together with its status code
import org.springframework.web.bind.annotation.ExceptionHandler;   //marks a method as handling a specific exception type
import org.springframework.web.bind.annotation.RestControllerAdvice;   //applies these handlers globally, across every controller

/**
 * This class catches specific exceptions thrown anywhere in the listing
 * service's request handling and turns them into proper HTTP error
 * responses, instead of a raw 500 error leaking internal details.
 *
 * - handleNotFound: turns a ListingNotFoundException into a 404 response
 *   with the exception's message as the body.
 * - handleNotOwner: turns a NotListingOwnerException into a 403 response
 *   with the exception's message as the body.
 */
@RestControllerAdvice   //applies to every controller in the app, no need to repeat this per-controller
public class ListingExceptionHandler {

    @ExceptionHandler(ListingNotFoundException.class)   //runs whenever this exception bubbles up from a controller
    public ResponseEntity<String> handleNotFound(ListingNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(NotListingOwnerException.class)   //runs whenever this exception bubbles up from a controller
    public ResponseEntity<String> handleNotOwner(NotListingOwnerException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

}