package com.vertere.bookingservice.booking;  //which folder/namespace this class belongs to

import java.util.UUID;   //the type used for the guest's id

import org.springframework.http.HttpStatus;   //used to explicitly set response status codes
import org.springframework.http.ResponseEntity;   //wraps a response body together with its status code
import org.springframework.security.core.Authentication;   //holds info about the currently authenticated caller, set by JwtAuthFilter
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

import com.vertere.bookingservice.booking.dto.BookingResponse;
import com.vertere.bookingservice.booking.dto.CreateBookingRequest;   //the shape of what we send back about a booking

import jakarta.validation.Valid;   //the shape of an incoming "create a booking" request

/**
 * This is the HTTP entry point for creating bookings - it translates
 * incoming requests into calls on BookingService and wraps the results
 * as HTTP responses. It doesn't contain business logic itself.
 *
 * - bookingService: does the actual work; this class just adapts HTTP
 *   in and out.
 * - create: POST a new booking, made by whoever is authenticated.
 */
@RestController   //marks this as a REST controller whose method return values become the response body
@RequestMapping("/bookings")   //every endpoint here is under /bookings
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {   //Spring automatically supplies this bean
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> create(
        Authentication authentication,   //injected by Spring Security based on the verified JWT
        @Valid @RequestBody CreateBookingRequest request   //validated against its annotations before this method runs
    ) {
        UUID guestUserId = UUID.fromString(authentication.getName());   //the "name" here is the user id we put in the token's subject
        BookingResponse response = bookingService.createBooking(guestUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);   //201 Created
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(
        @PathVariable UUID id,
        Authentication authentication
    ) {
        UUID requestingUserId = UUID.fromString(authentication.getName());
        bookingService.cancelBooking(id, requestingUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mine")
    public ResponseEntity<List<BookingResponse>> getMine(Authentication authentication) {
        UUID guestUserId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(bookingService.getMyBookings(guestUserId));
    }

}
