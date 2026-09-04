// ReviewsSection.jsx
// Shows a listing's reviews with an average rating up top. If the
// current user is the listing's host, an inline reply form appears
// under any review that doesn't have a host response yet.

import { useState } from "react";
import { respondToReview } from "../api/reviews";
import StarRating from "./StarRating";
import Button from "./Button";

function HostReplyForm({ reviewId, onReplied }) {
  const [text, setText] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();
    setSubmitting(true);
    setError("");
    try {
      const updated = await respondToReview(reviewId, text);
      onReplied(updated);
    } catch (err) {
      setError(err.message || "Could not post your response.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} className="mt-3 flex gap-2">
      <input
        type="text"
        placeholder="Write a response as the host..."
        value={text}
        onChange={(e) => setText(e.target.value)}
        className="flex-1 text-sm border border-brand-beige/60 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-brand-terracotta/40"
        required
      />
      <Button type="submit" variant="secondary" disabled={submitting}>
        {submitting ? "Posting..." : "Reply"}
      </Button>
      {error && <p className="text-red-600 text-xs self-center">{error}</p>}
    </form>
  );
}

function ReviewsSection({ reviews, isHost, onReviewUpdated }) {
  const average =
    reviews.length > 0 ? reviews.reduce((sum, r) => sum + r.rating, 0) / reviews.length : 0;

  return (
    <section className="pt-10 border-t border-brand-beige/40">
      <div className="flex items-center gap-2 mb-6">
        <StarRating value={Math.round(average)} size="text-lg" />
        <h2 className="text-lg font-semibold text-brand-dark">
          {reviews.length > 0
            ? `${average.toFixed(1)} · ${reviews.length} review${reviews.length > 1 ? "s" : ""}`
            : "No reviews yet"}
        </h2>
      </div>

      <div className="space-y-6">
        {reviews.map((review) => (
          <div key={review.id} className="pb-6 border-b border-brand-beige/30 last:border-b-0">
            <StarRating value={review.rating} />
            <p className="text-brand-dark mt-2">{review.comment}</p>

            {review.hostResponse ? (
              <div className="mt-3 ml-4 pl-4 border-l-2 border-brand-beige/50">
                <p className="text-xs font-medium text-brand-tan mb-1">Response from the host</p>
                <p className="text-sm text-brand-dark">{review.hostResponse}</p>
              </div>
            ) : (
              isHost && <HostReplyForm reviewId={review.id} onReplied={onReviewUpdated} />
            )}
          </div>
        ))}
      </div>
    </section>
  );
}

export default ReviewsSection;
