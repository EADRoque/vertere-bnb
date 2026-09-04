// ListingsPage.jsx
// Browse & search results. Reads filters from the URL (so a search is
// shareable/bookmarkable), fetches a page of listings from
// GET /listings/search, and renders them as a card grid with simple
// prev/next pagination.

import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { searchListings } from "../api/listings";
import ListingCard from "../components/ListingCard";

function ListingsPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [page, setPage] = useState(null); // the Spring Page object: { content, totalElements, totalPages, number, ... }
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const city = searchParams.get("city") || "";
  const pageNumber = Number(searchParams.get("page") || 0);

  useEffect(() => {
    let cancelled = false; // avoids setting state if the component unmounts (or params change) before the request finishes

    setLoading(true);
    setError("");

    searchListings({ city, page: pageNumber, size: 12 })
      .then((result) => {
        if (!cancelled) setPage(result);
      })
      .catch(() => {
        if (!cancelled) setError("Could not load listings. Please try again.");
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [city, pageNumber]);

  function goToPage(nextPage) {
    const next = new URLSearchParams(searchParams);
    next.set("page", nextPage);
    setSearchParams(next);
  }

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 py-10">
      <h1 className="text-2xl font-semibold text-brand-dark mb-6">
        {city ? `Stays in ${city}` : "All stays"}
      </h1>

      {loading && <p className="text-brand-tan">Loading listings...</p>}
      {error && <p className="text-red-600">{error}</p>}

      {!loading && !error && page && page.content.length === 0 && (
        <p className="text-brand-tan">No listings found. Try a different search.</p>
      )}

      {!loading && !error && page && page.content.length > 0 && (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {page.content.map((listing) => (
              <ListingCard key={listing.id} listing={listing} />
            ))}
          </div>

          {page.totalPages > 1 && (
            <div className="mt-10 flex items-center justify-center gap-4">
              <button
                onClick={() => goToPage(pageNumber - 1)}
                disabled={pageNumber === 0}
                className="px-4 py-2 rounded-full border border-brand-tan text-brand-dark disabled:opacity-40 disabled:cursor-not-allowed hover:bg-brand-beige/20 transition-colors duration-150"
              >
                Previous
              </button>
              <span className="text-sm text-brand-tan">
                Page {pageNumber + 1} of {page.totalPages}
              </span>
              <button
                onClick={() => goToPage(pageNumber + 1)}
                disabled={pageNumber + 1 >= page.totalPages}
                className="px-4 py-2 rounded-full border border-brand-tan text-brand-dark disabled:opacity-40 disabled:cursor-not-allowed hover:bg-brand-beige/20 transition-colors duration-150"
              >
                Next
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}

export default ListingsPage;
