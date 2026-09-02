package com.vertere.bookingservice.booking;

import com.vertere.bookingservice.booking.dto.BookingResponse;
import com.vertere.bookingservice.booking.dto.CreateBookingRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> create(
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateBookingRequest request
    ) {
        UUID guestUserId = UUID.fromString(authentication.getName());
        BookingResponse response = bookingService.createBooking(guestUserId, authHeader, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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