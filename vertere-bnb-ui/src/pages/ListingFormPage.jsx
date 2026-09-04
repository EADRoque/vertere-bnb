// ListingFormPage.jsx
// One form, two modes: create a new listing (route: /host/listings/new)
// or edit an existing one (route: /host/listings/:id/edit). Amenities
// can only be set at creation time - UpdateListingRequest on the backend
// doesn't accept them - so that field only appears in create mode.

import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getListing, createListing, updateListing } from "../api/listings";
import Spinner from "../components/Spinner";
import Button from "../components/Button";

const EMPTY_FORM = {
  title: "",
  description: "",
  propertyType: "",
  city: "",
  country: "",
  maxGuests: 1,
  basePrice: "",
  cleaningFee: "",
  cancellationPolicy: "FLEXIBLE",
  amenityNames: "",
};

function ListingFormPage() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();

  const [form, setForm] = useState(EMPTY_FORM);
  const [loading, setLoading] = useState(isEdit);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!isEdit) return;
    getListing(id).then((listing) => {
      setForm({
        title: listing.title,
        description: listing.description,
        propertyType: listing.propertyType,
        city: listing.city,
        country: listing.country,
        maxGuests: listing.maxGuests,
        basePrice: listing.basePrice,
        cleaningFee: listing.cleaningFee,
        cancellationPolicy: listing.cancellationPolicy,
        amenityNames: "",
      });
      setLoading(false);
    });
  }, [id, isEdit]);

  function update(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setSubmitting(true);
    setError("");

    const payload = {
      title: form.title,
      description: form.description,
      propertyType: form.propertyType,
      city: form.city,
      country: form.country,
      maxGuests: Number(form.maxGuests),
      basePrice: Number(form.basePrice),
      cleaningFee: Number(form.cleaningFee),
      cancellationPolicy: form.cancellationPolicy,
    };

    try {
      if (isEdit) {
        await updateListing(id, payload);
        navigate(`/listings/${id}`);
      } else {
        const amenityNames = form.amenityNames
          .split(",")
          .map((a) => a.trim())
          .filter(Boolean);
        const created = await createListing({ ...payload, amenityNames });
        navigate(`/listings/${created.id}`);
      }
    } catch (err) {
      setError(err.message || "Could not save this listing.");
    } finally {
      setSubmitting(false);
    }
  }

  if (loading) return <Spinner label="Loading listing..." />;

  const inputClass =
    "w-full text-sm border border-brand-beige/60 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-brand-terracotta/40 transition-colors duration-150";
  const labelClass = "block text-sm font-medium text-brand-dark mb-1.5";

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 py-10">
      <h1 className="text-2xl font-semibold text-brand-dark mb-8">
        {isEdit ? "Edit listing" : "Create a new listing"}
      </h1>

      <form onSubmit={handleSubmit} className="p-8 rounded-2xl border border-brand-beige/40 bg-white shadow-sm space-y-5">
        <div>
          <label className={labelClass}>Title</label>
          <input className={inputClass} value={form.title} onChange={(e) => update("title", e.target.value)} required />
        </div>

        <div>
          <label className={labelClass}>Description</label>
          <textarea
            className={`${inputClass} resize-none`}
            rows={4}
            value={form.description}
            onChange={(e) => update("description", e.target.value)}
            required
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className={labelClass}>Property type</label>
            <input
              className={inputClass}
              placeholder="Apartment, House..."
              value={form.propertyType}
              onChange={(e) => update("propertyType", e.target.value)}
              required
            />
          </div>
          <div>
            <label className={labelClass}>Max guests</label>
            <input
              type="number"
              min="1"
              className={inputClass}
              value={form.maxGuests}
              onChange={(e) => update("maxGuests", e.target.value)}
              required
            />
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className={labelClass}>City</label>
            <input className={inputClass} value={form.city} onChange={(e) => update("city", e.target.value)} required />
          </div>
          <div>
            <label className={labelClass}>Country</label>
            <input className={inputClass} value={form.country} onChange={(e) => update("country", e.target.value)} required />
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className={labelClass}>Nightly price ($)</label>
            <input
              type="number"
              step="0.01"
              min="0"
              className={inputClass}
              value={form.basePrice}
              onChange={(e) => update("basePrice", e.target.value)}
              required
            />
          </div>
          <div>
            <label className={labelClass}>Cleaning fee ($)</label>
            <input
              type="number"
              step="0.01"
              min="0"
              className={inputClass}
              value={form.cleaningFee}
              onChange={(e) => update("cleaningFee", e.target.value)}
              required
            />
          </div>
        </div>

        <div>
          <label className={labelClass}>Cancellation policy</label>
          <select
            className={inputClass}
            value={form.cancellationPolicy}
            onChange={(e) => update("cancellationPolicy", e.target.value)}
          >
            <option value="FLEXIBLE">Flexible</option>
            <option value="MODERATE">Moderate</option>
            <option value="STRICT">Strict</option>
          </select>
        </div>

        {!isEdit && (
          <div>
            <label className={labelClass}>Amenities (comma-separated)</label>
            <input
              className={inputClass}
              placeholder="WiFi, Pool, Kitchen"
              value={form.amenityNames}
              onChange={(e) => update("amenityNames", e.target.value)}
            />
          </div>
        )}

        {error && <p className="text-sm text-red-600">{error}</p>}

        <Button type="submit" disabled={submitting} className="w-full">
          {submitting ? "Saving..." : isEdit ? "Save changes" : "Create listing"}
        </Button>
      </form>
    </div>
  );
}

export default ListingFormPage;
