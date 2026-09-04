// reviews.js
// API calls for leaving reviews on a stay and, as a host, responding to
// reviews left on your listings.

import { apiRequest } from "./client";

export function createReview(data) {
  // data: { listingId, bookingId, rating (1-5), comment }
  return apiRequest("/reviews", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export function respondToReview(reviewId, response) {
  return apiRequest(`/reviews/${reviewId}/response`, {
    method: "PUT",
    body: JSON.stringify({ response }),
  });
}

export function getListingReviews(listingId) {
  return apiRequest(`/listings/${listingId}/reviews`);
}
