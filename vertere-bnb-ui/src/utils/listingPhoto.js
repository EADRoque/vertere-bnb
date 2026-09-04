// listingPhoto.js
// listing-service has no photo field at all - CreateListingRequest never
// included one, and adding one is a backend change out of scope here.
// Instead, each listing gets a stable placeholder photo from Picsum,
// seeded by the listing's own id so the same listing always gets the
// same image (and different listings look different) without needing
// any backend support.

export function listingPhotoUrl(listingId, width = 800, height = 600) {
  return `https://picsum.photos/seed/${listingId}/${width}/${height}`;
}
