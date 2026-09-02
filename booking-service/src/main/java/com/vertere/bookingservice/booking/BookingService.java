package com.vertere.bookingservice.booking;

import com.vertere.bookingservice.booking.dto.BookingResponse;
import com.vertere.bookingservice.booking.dto.CreateBookingRequest;
import com.vertere.bookingservice.booking.exception.BookingNotFoundException;
import com.vertere.bookingservice.booking.exception.DatesUnavailableException;
import com.vertere.bookingservice.booking.exception.ListingServiceUnavailableException;
import com.vertere.bookingservice.booking.exception.NotBookingOwnerException;
import com.vertere.bookingservice.client.ListingClient;
import com.vertere.bookingservice.client.PaymentClient;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ListingClient listingClient;
    private final PaymentClient paymentClient;

    public BookingService(BookingRepository bookingRepository, ListingClient listingClient, PaymentClient paymentClient) {
        this.bookingRepository = bookingRepository;
        this.listingClient = listingClient;
        this.paymentClient = paymentClient;
    }

    public BookingResponse createBooking(UUID guestUserId, String authHeader, CreateBookingRequest request) {
        List<LocalDate> blockedDates;
        BigDecimal nightlyRate;

        try {
            blockedDates = listingClient.getBlockedDates(request.listingId(), request.checkIn(), request.checkOut(), authHeader);
            nightlyRate = listingClient.getBasePrice(request.listingId(), authHeader);
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
            paymentSucceeded = paymentClient.charge(saved.getId(), saved.getTotalAmount(), authHeader);
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

    private BookingResponse toResponse(Booking booking) {
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