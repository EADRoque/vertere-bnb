package com.vertere.bookingservice.booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.vertere.bookingservice.booking.dto.BookingResponse;
import com.vertere.bookingservice.booking.dto.CreateBookingRequest;
import com.vertere.bookingservice.booking.exception.BookingNotFoundException;
import com.vertere.bookingservice.booking.exception.DatesUnavailableException;
import com.vertere.bookingservice.booking.exception.ListingServiceUnavailableException;
import com.vertere.bookingservice.booking.exception.NotBookingOwnerException;
import com.vertere.bookingservice.client.ListingClient;
import com.vertere.bookingservice.client.PaymentClient;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ListingClient listingClient;

    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private BookingService bookingService;

    private UUID guestId;
    private UUID listingId;
    private static final String AUTH_HEADER = "Bearer test-token";

    @BeforeEach
    void setUp() {
        guestId = UUID.randomUUID();
        listingId = UUID.randomUUID();
    }

    @Test
    void createBooking_succeeds_whenPaymentSucceeds() {
        CreateBookingRequest request = new CreateBookingRequest(listingId, LocalDate.of(2026, 6, 10), LocalDate.of(2026, 6, 15));

        when(listingClient.getBlockedDates(any(), any(), any(), any())).thenReturn(List.of());
        when(listingClient.getBasePrice(any(), any())).thenReturn(new BigDecimal("100.00"));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentClient.charge(any(), any(), any())).thenReturn(true);

        BookingResponse response = bookingService.createBooking(guestId, AUTH_HEADER, request);

        assertEquals(new BigDecimal("500.00"), response.totalAmount());
        assertEquals("CONFIRMED", response.status());
    }

    @Test
    void createBooking_cancelsBooking_whenPaymentDeclined() {
        CreateBookingRequest request = new CreateBookingRequest(listingId, LocalDate.of(2026, 6, 10), LocalDate.of(2026, 6, 15));

        when(listingClient.getBlockedDates(any(), any(), any(), any())).thenReturn(List.of());
        when(listingClient.getBasePrice(any(), any())).thenReturn(new BigDecimal("100.00"));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentClient.charge(any(), any(), any())).thenReturn(false);

        BookingResponse response = bookingService.createBooking(guestId, AUTH_HEADER, request);

        assertEquals("CANCELLED", response.status());
    }

    @Test
    void createBooking_throwsException_whenDatesAreBlocked() {
        CreateBookingRequest request = new CreateBookingRequest(listingId, LocalDate.of(2026, 6, 10), LocalDate.of(2026, 6, 15));

        when(listingClient.getBlockedDates(any(), any(), any(), any())).thenReturn(List.of(LocalDate.of(2026, 6, 12)));

        assertThrows(DatesUnavailableException.class, () -> bookingService.createBooking(guestId, AUTH_HEADER, request));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_throwsException_whenListingServiceFails() {
        CreateBookingRequest request = new CreateBookingRequest(listingId, LocalDate.of(2026, 6, 10), LocalDate.of(2026, 6, 15));

        when(listingClient.getBlockedDates(any(), any(), any(), any())).thenThrow(new RuntimeException("connection refused"));

        assertThrows(ListingServiceUnavailableException.class, () -> bookingService.createBooking(guestId, AUTH_HEADER, request));
    }

    @Test
    void createBooking_throwsException_whenDatabaseRejectsOverlap() {
        CreateBookingRequest request = new CreateBookingRequest(listingId, LocalDate.of(2026, 6, 10), LocalDate.of(2026, 6, 15));

        when(listingClient.getBlockedDates(any(), any(), any(), any())).thenReturn(List.of());
        when(listingClient.getBasePrice(any(), any())).thenReturn(new BigDecimal("100.00"));
        when(bookingRepository.save(any(Booking.class))).thenThrow(new DataIntegrityViolationException("overlap"));

        assertThrows(DatesUnavailableException.class, () -> bookingService.createBooking(guestId, AUTH_HEADER, request));
    }

    @Test
    void cancelBooking_throwsException_whenNotOwner() {
        Booking booking = new Booking(listingId, guestId, LocalDate.of(2026, 6, 10), LocalDate.of(2026, 6, 15), new BigDecimal("500.00"));
        UUID bookingId = UUID.randomUUID();
        UUID someoneElseId = UUID.randomUUID();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotBookingOwnerException.class, () -> bookingService.cancelBooking(bookingId, someoneElseId));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void cancelBooking_throwsException_whenBookingNotFound() {
        UUID bookingId = UUID.randomUUID();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.cancelBooking(bookingId, guestId));
    }

}