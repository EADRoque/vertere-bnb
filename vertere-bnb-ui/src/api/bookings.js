// bookings.js
// API calls for creating and managing the current guest's bookings.
// Note: charging the guest happens automatically on the backend as part
// of creating a booking (booking-service calls payment-service itself) -
// the frontend never calls the payments endpoints directly.

import { apiRequest } from "./client";

export function createBooking(data) {
  // data: { listingId, checkIn, checkOut }
  // resolves to a BookingResponse whose status is "CONFIRMED" if payment
  // succeeded, or "CANCELLED" if the simulated charge was declined.
  return apiRequest("/bookings", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export function getMyBookings() {
  return apiRequest("/bookings/mine");
}

export function cancelBooking(id) {
  return apiRequest(`/bookings/${id}`, { method: "DELETE" });
}
