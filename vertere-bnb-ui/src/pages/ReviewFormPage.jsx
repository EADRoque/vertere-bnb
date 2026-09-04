// ReviewFormPage.jsx
// Reached from "Leave a review" on the My trips page - takes a
// listingId (in the URL path) and a bookingId (in the query string) and
// posts a new review against that stay.

import { useState } from "react";
import { useParams, useSearchParams, useNavigate } from "react-router-dom";
import { createReview } from "../api/reviews";
import StarRating from "../components/StarRating";
import Button from "../components/Button";

function ReviewFormPage() {
  const { id: listingId } = useParams();
  const [searchParams] = useSearchParams();
  const bookingId = searchParams.get("bookingId");
  const navigate = useNavigate();

  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();
    setSubmitting(true);
    setError("");
    try {
      await createReview({ listingId, bookingId, rating, comment });
      navigate(`/listings/${listingId}`);
    } catch (err) {
      setError(err.message || "Could not submit your review.");
    } finally {
      setSubmitting(false);
    }
  }

  if (!bookingId) {
    return <p className="text-center text-red-600 py-16">Missing booking reference.</p>;
  }

  return (
    <div className="max-w-md mx-auto px-4 sm:px-6 py-16">
      <div className="p-8 rounded-2xl border border-brand-beige/40 bg-white shadow-sm">
        <h1 className="text-2xl font-semibold text-brand-dark mb-6">How was your stay?</h1>

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <p className="text-sm text-brand-tan mb-2">Your rating</p>
            <StarRating value={rating} onChange={setRating} size="text-2xl" />
          </div>

          <textarea
            placeholder="Share details about your stay..."
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            rows={5}
            className="w-full text-sm border border-brand-beige/60 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-brand-terracotta/40 resize-none"
            required
          />

          {error && <p className="text-sm text-red-600">{error}</p>}

          <Button type="submit" disabled={submitting} className="w-full">
            {submitting ? "Submitting..." : "Submit review"}
          </Button>
        </form>
      </div>
    </div>
  );
}

export default ReviewFormPage;
