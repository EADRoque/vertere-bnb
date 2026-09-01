package com.vertere.bookingservice.booking;  //which folder/namespace this class belongs to

import java.math.BigDecimal;   //precise number type used for money (nightly rate, total amount)
import java.time.Instant;
import java.time.LocalDate;   //represents a calendar date with no time component
import java.time.temporal.ChronoUnit;   //used to count the number of nights between two dates
import java.util.List;   //the collection type used for blocked dates
import java.util.UUID;   //the type used for guest/listing/booking ids
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;   //thrown when the database rejects a save due to a constraint violation
import org.springframework.stereotype.Service;   //tells Spring "this class holds business logic, manage it as a bean"

import com.vertere.bookingservice.booking.dto.BookingResponse;   //the shape of what we send back about a booking
import com.vertere.bookingservice.booking.dto.CreateBookingRequest;   //the shape of an incoming "create a booking" request
import com.vertere.bookingservice.booking.exception.BookingNotFoundException;   //thrown when the requested dates can't be booked
import com.vertere.bookingservice.booking.exception.DatesUnavailableException;
import com.vertere.bookingservice.booking.exception.ListingServiceUnavailableException;
import com.vertere.bookingservice.booking.exception.NotBookingOwnerException;
import com.vertere.bookingservice.client.ListingClient;   //talks to listing-service over HTTP to check prices/availability
import com.vertere.bookingservice.client.PaymentClient;

/**
 * This class holds the actual business logic for creating bookings -
 * the controller layer calls into here instead of talking to the
 * database or listing-service directly.
 *
 * - bookingRepository: how this service reads/writes Booking rows in
 *   the database.
 * - listingClient: how this service asks listing-service (a separate
 *   microservice) for a listing's price and blocked dates.
 * - createBooking: checks the host-blocked dates for the requested
 *   range, calculates the total price from the listing's nightly rate,
 *   saves the booking, and returns it as a BookingResponse. The database
 *   itself is the final safety net against double-booking (see the
 *   no_overlapping_bookings constraint) - if two guests race to book the
 *   same dates, the losing save fails and is turned into the same
 *   "unavailable" error.
 * - toResponse: a small private helper that converts our internal
 *   Booking entity into the safe, public-facing BookingResponse shape.
 */
@Service   //makes this class a Spring-managed bean so it can be injected elsewhere (e.g. into a controller)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ListingClient listingClient;
    private final PaymentClient paymentClient;


    public BookingService(BookingRepository bookingRepository, ListingClient listingClient, PaymentClient paymentClient) {   //Spring automatically supplies these beans
        this.bookingRepository = bookingRepository;
        this.listingClient = listingClient;
        this.paymentClient = paymentClient;
    }

    public BookingResponse createBooking(UUID guestUserId, CreateBookingRequest request) {
        List<LocalDate> blockedDates;
        BigDecimal nightlyRate;

        try {
            blockedDates = listingClient.getBlockedDates(request.listingId(), request.checkIn(), request.checkOut());
            nightlyRate = listingClient.getBasePrice(request.listingId());
        } catch (Exception e) {
            throw new ListingServiceUnavailableException("Listing service is currently unavailable, please try again");
        }

        if (!blockedDates.isEmpty()) {
            throw new DatesUnavailableException("The host has blocked some of these dates");
        }

        long nights = ChronoUnit.DAYS.between(request.checkIn(), request.checkOut());
        BigDecimal totalAmount = nightlyRate.multiply(BigDecimal.valueOf(nights));

        Booking booking = new Booking(
                request.listingId(),
                guestUserId,
                request.checkIn(),
                request.checkOut(),
                totalAmount
        );

        Booking saved;
        try {
            saved = bookingRepository.save(booking);
        } catch (DataIntegrityViolationException e) {
            throw new DatesUnavailableException("These dates are no longer available for this listing");
        }

        boolean paymentSucceeded;
        try {
            paymentSucceeded = paymentClient.charge(saved.getId(), saved.getTotalAmount());
        } catch (Exception e) {
            paymentSucceeded = false;
        }

        if (paymentSucceeded) {
            saved.setStatus("CONFIRMED");
        } else {
            saved.setStatus("CANCELLED");
            saved.setCancelledAt(Instant.now());
        }

        Booking finalBooking = bookingRepository.save(saved);
        return toResponse(finalBooking);
}

    public void cancelBooking(UUID bookingId, UUID requestingUserId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (!booking.getGuestUserId().equals(requestingUserId)) {
            throw new NotBookingOwnerException("You do not own this booking");
        }

        booking.setStatus("CANCELLED");
        booking.setCancelledAt(Instant.now());
        bookingRepository.save(booking);
    }

    public List<BookingResponse> getMyBookings(UUID guestUserId) {
        return bookingRepository.findByGuestUserId(guestUserId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private BookingResponse toResponse(Booking booking) {   //maps a Booking entity to a BookingResponse
        return new BookingResponse(
            booking.getId(),
            booking.getListingId(),
            booking.getGuestUserId(),
            booking.getCheckIn(),
            booking.getCheckOut(),
            booking.getStatus(),
            booking.getTotalAmount(),
            booking.getCurrency(),
            booking.getCreatedAt()
        );
    }
}
