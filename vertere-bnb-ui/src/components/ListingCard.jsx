// ListingCard.jsx
// A single listing preview shown in the search results grid.

import { Link } from "react-router-dom";
import { listingPhotoUrl } from "../utils/listingPhoto";

function ListingCard({ listing }) {
  return (
    <Link
      to={`/listings/${listing.id}`}
      className="group block rounded-2xl overflow-hidden border border-brand-beige/30 bg-white hover:shadow-lg transition-shadow duration-200"
    >
      <div className="aspect-4/3 bg-brand-beige/30 overflow-hidden">
        <img
          src={listingPhotoUrl(listing.id, 400, 300)}
          alt={listing.title}
          loading="lazy"
          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
        />
      </div>

      <div className="p-4 space-y-1">
        <div className="flex items-start justify-between gap-2">
          <h3 className="font-medium text-brand-dark truncate">{listing.title}</h3>
        </div>
        <p className="text-sm text-brand-tan">
          {listing.city}, {listing.country}
        </p>
        <p className="text-sm text-brand-dark">
          <span className="font-semibold">${listing.basePrice}</span> / night
        </p>
      </div>
    </Link>
  );
}

export default ListingCard;
