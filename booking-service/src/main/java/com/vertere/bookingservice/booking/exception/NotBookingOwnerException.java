package com.vertere.bookingservice.booking.exception;

public class NotBookingOwnerException extends RuntimeException {
    public NotBookingOwnerException(String message) {
        super(message);
    }
}
