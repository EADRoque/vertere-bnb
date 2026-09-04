// BookingPanel.jsx
// The sticky card on a listing's detail page where a guest picks dates
// and reserves. Shown only to guests (the host sees host tools instead -
// see ListingDetailPage).

import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createBooking } from "../api/bookings";
import { useAuth } from "../context/AuthContext";
import Button from "./Button";

function nightsBetween(checkIn, checkOut) {
  if (!checkIn || !checkOut) return 0;
  const ms = new Date(checkOut) - new Date(checkIn);
  const nights = Math.round(ms / (1000 * 60 * 60 * 24));
  return nights > 0 ? nights : 0;
}

function BookingPanel({ listing }) {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [checkIn, setCheckIn] = useState("");
  const [checkOut, setCheckOut] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [result, setResult] = useState(null); // the BookingResponse once submitted

  const nights = useMemo(() => nightsBetween(checkIn, checkOut), [checkIn, checkOut]);
  const subtotal = nights * listing.basePrice;
  const total = nights > 0 ? subtotal + listing.cleaningFee : 0;

  async function handleReserve(e) {
    e.preventDefault();
    setError("");

    if (!user) {
      navigate("/login");
      return;
    }

    setSubmitting(true);
    try {
      const booking = await createBooking({ listingId: listing.id, checkIn, checkOut });
      setResult(booking);
    } catch (err) {
      setError(err.message || "Something went wrong. Please try again.");
    } finally {
      setSubmitting(false);
    }
  }

  if (result) {
    const confirmed = result.status === "CONFIRMED";
    return (
      <div className="rounded-2xl border border-brand-beige/40 bg-white p-6 shadow-sm">
        <p className={`text-lg font-semibold ${confirmed ? "text-emerald-700" : "text-rose-700"}`}>
          {confirmed ? "Booking confirmed!" : "Payment declined"}
        </p>
        <p className="text-sm text-brand-tan mt-2">
          {confirmed
            ? `You're all set for ${result.checkIn} → ${result.checkOut}.`
            : "The simulated charge for this booking was declined. No booking was made - feel free to try again."}
        </p>
        <div className="mt-5 flex gap-3">
          {confirmed ? (
            <Button to="/trips">View my trips</Button>
          ) : (
            <Button variant="secondary" onClick={() => setResult(null)}>Try again</Button>
          )}
        </div>
      </div>
    );
  }

  return (
    <div className="rounded-2xl border border-brand-beige/40 bg-white p-6 shadow-sm">
      <div className="flex items-baseline gap-1 mb-4">
        <span className="text-xl font-semibold text-brand-dark">${listing.basePrice}</span>
        <span className="text-sm text-brand-tan">/ night</span>
      </div>

      <form onSubmit={handleReserve} className="space-y-3">
        <div className="grid grid-cols-2 gap-2 border border-brand-beige/60 rounded-xl overflow-hidden">
          <label className="p-2 border-r border-brand-beige/60">
            <span className="block text-[10px] uppercase tracking-wide text-brand-tan">Check-in</span>
            <input
              type="date"
              value={checkIn}
              onChange={(e) => setCheckIn(e.target.value)}
              className="w-full text-sm text-brand-dark outline-none"
              required
            />
          </label>
          <label className="p-2">
            <span className="block text-[10px] uppercase tracking-wide text-brand-tan">Check-out</span>
            <input
              type="date"
              value={checkOut}
              onChange={(e) => setCheckOut(e.target.value)}
              min={checkIn || undefined}
              className="w-full text-sm text-brand-dark outline-none"
              required
            />
          </label>
        </div>

        {error && <p className="text-sm text-red-600">{error}</p>}

        <Button type="submit" disabled={submitting || nights <= 0} className="w-full">
          {submitting ? "Reserving..." : user ? "Reserve" : "Log in to reserve"}
        </Button>

        {nights > 0 && (
          <div className="pt-3 space-y-2 text-sm text-brand-dark border-t border-brand-beige/40">
            <div className="flex justify-between text-brand-tan">
              <span>${listing.basePrice} × {nights} night{nights > 1 ? "s" : ""}</span>
              <span>${subtotal.toFixed(2)}</span>
            </div>
            <div className="flex justify-between text-brand-tan">
              <span>Cleaning fee</span>
              <span>${Number(listing.cleaningFee).toFixed(2)}</span>
            </div>
            <div className="flex justify-between font-semibold pt-2 border-t border-brand-beige/40">
              <span>Total</span>
              <span>${total.toFixed(2)}</span>
            </div>
          </div>
        )}
      </form>
    </div>
  );
}

export default BookingPanel;
