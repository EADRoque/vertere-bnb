// MyListingsPage.jsx
// A host's own listings. There's no dedicated "GET /listings/mine"
// endpoint on the backend, so this reuses the public search endpoint
// (a generous page size) and filters down to listings owned by the
// current user. Because search only ever returns *active* listings,
// a listing the host has deactivated will disappear from this page too
// - a known gap until the backend adds a real "mine" endpoint.

import { useEffect, useState } from "react";
import { searchListings } from "../api/listings";
import { useAuth } from "../context/AuthContext";
import Spinner from "../components/Spinner";
import EmptyState from "../components/EmptyState";
import Button from "../components/Button";
import ListingCard from "../components/ListingCard";

function MyListingsPage() {
  const { user } = useAuth();
  const [listings, setListings] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    searchListings({ size: 100 })
      .then((page) => setListings(page.content.filter((l) => l.hostUserId === user.id)))
      .finally(() => setLoading(false));
  }, [user.id]);

  if (loading) return <Spinner label="Loading your listings..." />;

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 py-10">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-2xl font-semibold text-brand-dark">Your listings</h1>
        <Button to="/host/listings/new">+ New listing</Button>
      </div>

      {listings.length === 0 ? (
        <EmptyState
          title="You haven't listed a place yet"
          description="Create your first listing to start hosting."
          action={<Button to="/host/listings/new">Create a listing</Button>}
        />
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {listings.map((listing) => (
            <ListingCard key={listing.id} listing={listing} />
          ))}
        </div>
      )}
    </div>
  );
}

export default MyListingsPage;
