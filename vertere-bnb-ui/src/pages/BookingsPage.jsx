// BookingsPage.jsx
// "My trips" - every booking the current guest has made. BookingResponse
// only carries a listingId, not the listing's title/city/photo, so this
// page fetches each distinct listing once and joins them in memory.

import { useEffect, useState } from "react";
import { getMyBookings, cancelBooking } from "../api/bookings";
import { getListing } from "../api/listings";
import Spinner from "../components/Spinner";
import EmptyState from "../components/EmptyState";
import StatusBadge from "../components/StatusBadge";
import Button from "../components/Button";

function isPast(dateStr) {
  return new Date(dateStr) < new Date();
}

function BookingsPage() {
  const [bookings, setBookings] = useState(null);
  const [listingsById, setListingsById] = useState({});
  const [loading, setLoading] = useState(true);
  const [cancellingId, setCancellingId] = useState(null);

  useEffect(() => {
    let cancelled = false;

    getMyBookings()
      .then(async (data) => {
        if (cancelled) return;
        setBookings(data);

        const uniqueListingIds = [...new Set(data.map((b) => b.listingId))];
        const listingResults = await Promise.all(
          uniqueListingIds.map((id) => getListing(id).catch(() => null))
        );
        if (cancelled) return;

        const map = {};
        uniqueListingIds.forEach((id, i) => {
          if (listingResults[i]) map[id] = listingResults[i];
        });
        setListingsById(map);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, []);

  async function handleCancel(id) {
    if (!window.confirm("Cancel this booking?")) return;
    setCancellingId(id);
    try {
      await cancelBooking(id);
      setBookings((prev) => prev.map((b) => (b.id === id ? { ...b, status: "CANCELLED" } : b)));
    } finally {
      setCancellingId(null);
    }
  }

  if (loading) return <Spinner label="Loading your trips..." />;

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 py-10">
      <h1 className="text-2xl font-semibold text-brand-dark mb-8">My trips</h1>

      {bookings.length === 0 ? (
        <EmptyState
          title="No trips booked yet"
          description="Once you book a stay, it'll show up here."
          action={<Button to="/listings">Browse stays</Button>}
        />
      ) : (
        <div className="space-y-4">
          {bookings.map((booking) => {
            const listing = listingsById[booking.listingId];
            const canCancel = booking.status !== "CANCELLED" && !isPast(booking.checkIn);
            const canReview = booking.status === "CONFIRMED" && isPast(booking.checkOut);

            return (
              <div
                key={booking.id}
                className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 p-5 rounded-2xl border border-brand-beige/40 bg-white shadow-sm"
              >
                <div>
                  <div className="flex items-center gap-2 mb-1">
                    <p className="font-medium text-brand-dark">
                      {listing ? listing.title : "Listing unavailable"}
                    </p>
                    <StatusBadge status={booking.status} />
                  </div>
                  {listing && <p className="text-sm text-brand-tan">{listing.city}, {listing.country}</p>}
                  <p className="text-sm text-brand-tan mt-1">
                    {booking.checkIn} → {booking.checkOut} · ${booking.totalAmount} {booking.currency}
                  </p>
                </div>

                <div className="flex gap-2 shrink-0">
                  {canReview && listing && (
                    <Button variant="secondary" to={`/listings/${listing.id}/review?bookingId=${booking.id}`}>
                      Leave a review
                    </Button>
                  )}
                  {canCancel && (
                    <Button
                      variant="danger"
                      onClick={() => handleCancel(booking.id)}
                      disabled={cancellingId === booking.id}
                    >
                      {cancellingId === booking.id ? "Cancelling..." : "Cancel"}
                    </Button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

export default BookingsPage;
