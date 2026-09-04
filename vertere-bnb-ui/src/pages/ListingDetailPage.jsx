// ListingDetailPage.jsx
// The full page for a single listing: photo area, key facts, amenities,
// reviews, and either a booking panel (for guests) or host tools (for
// the listing's own host).

import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getListing } from "../api/listings";
import { getListingReviews } from "../api/reviews";
import { useAuth } from "../context/AuthContext";
import { listingPhotoUrl } from "../utils/listingPhoto";
import BookingPanel from "../components/BookingPanel";
import HostToolsPanel from "../components/HostToolsPanel";
import ContactHostForm from "../components/ContactHostForm";
import ReviewsSection from "../components/ReviewsSection";
import Spinner from "../components/Spinner";

function ListingDetailPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();

  const [listing, setListing] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError("");

    Promise.all([getListing(id), getListingReviews(id)])
      .then(([listingData, reviewData]) => {
        if (cancelled) return;
        setListing(listingData);
        setReviews(reviewData);
      })
      .catch(() => {
        if (!cancelled) setError("This listing couldn't be found.");
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [id]);

  function handleReviewUpdated(updatedReview) {
    setReviews((prev) => prev.map((r) => (r.id === updatedReview.id ? updatedReview : r)));
  }

  if (loading) return <Spinner label="Loading listing..." />;
  if (error) return <p className="text-center text-red-600 py-16">{error}</p>;
  if (!listing) return null;

  const isHost = user && listing.hostUserId === user.id;

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 py-10">
      {/* listing-service has no photo field, so this is a Picsum placeholder seeded by the listing's id - stable per listing, not a real photo */}
      <div className="aspect-16/7 rounded-2xl bg-brand-beige/30 overflow-hidden mb-8">
        <img
          src={listingPhotoUrl(listing.id, 1600, 700)}
          alt={listing.title}
          className="w-full h-full object-cover"
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-10">
        <div className="lg:col-span-2">
          <h1 className="text-2xl sm:text-3xl font-semibold text-brand-dark">{listing.title}</h1>
          <p className="text-brand-tan mt-1">
            {listing.city}, {listing.country} · Up to {listing.maxGuests} guests
          </p>

          {!listing.active && (
            <p className="mt-3 inline-block text-xs font-medium px-2.5 py-1 rounded-full bg-rose-50 text-rose-700 border border-rose-200">
              This listing is currently deactivated
            </p>
          )}

          <p className="mt-6 text-brand-dark leading-relaxed whitespace-pre-line">{listing.description}</p>

          {listing.amenities?.length > 0 && (
            <div className="mt-8 pt-8 border-t border-brand-beige/40">
              <h2 className="text-lg font-semibold text-brand-dark mb-4">What this place offers</h2>
              <div className="flex flex-wrap gap-2">
                {listing.amenities.map((amenity) => (
                  <span
                    key={amenity}
                    className="text-sm px-3 py-1.5 rounded-full bg-brand-beige/20 text-brand-dark"
                  >
                    {amenity}
                  </span>
                ))}
              </div>
            </div>
          )}

          <div className="mt-8 pt-8 border-t border-brand-beige/40">
            <h2 className="text-lg font-semibold text-brand-dark mb-2">Cancellation policy</h2>
            <p className="text-sm text-brand-tan">{listing.cancellationPolicy}</p>
          </div>

          {!isHost && (
            <div className="mt-8 pt-8 border-t border-brand-beige/40">
              <ContactHostForm listing={listing} />
            </div>
          )}

          <ReviewsSection reviews={reviews} isHost={isHost} onReviewUpdated={handleReviewUpdated} />
        </div>

        <div className="lg:col-span-1">
          <div className="lg:sticky lg:top-20">
            {isHost ? (
              <HostToolsPanel listing={listing} onDeactivated={() => navigate("/host/listings")} />
            ) : (
              <BookingPanel listing={listing} />
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default ListingDetailPage;
