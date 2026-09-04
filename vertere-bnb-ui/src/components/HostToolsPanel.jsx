// HostToolsPanel.jsx
// Replaces the guest BookingPanel when the current user is viewing their
// own listing. Lets the host edit/deactivate the listing, and manage
// availability (block dates, set custom prices) for the next 60 days.

import { useEffect, useState } from "react";
import { getAvailability, blockDate, setPriceOverride, deactivateListing } from "../api/listings";
import Button from "./Button";

function todayPlus(days) {
  const d = new Date();
  d.setDate(d.getDate() + days);
  return d.toISOString().slice(0, 10);
}

function HostToolsPanel({ listing, onDeactivated }) {
  const [availability, setAvailability] = useState(null);
  const [blockInput, setBlockInput] = useState("");
  const [priceDate, setPriceDate] = useState("");
  const [priceValue, setPriceValue] = useState("");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  const rangeStart = todayPlus(0);
  const rangeEnd = todayPlus(60);

  function refreshAvailability() {
    getAvailability(listing.id, rangeStart, rangeEnd).then(setAvailability);
  }

  useEffect(() => {
    refreshAvailability();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [listing.id]);

  async function handleBlock(e) {
    e.preventDefault();
    setError("");
    setBusy(true);
    try {
      await blockDate(listing.id, blockInput);
      setBlockInput("");
      refreshAvailability();
    } catch (err) {
      setError(err.message || "Could not block that date.");
    } finally {
      setBusy(false);
    }
  }

  async function handlePriceOverride(e) {
    e.preventDefault();
    setError("");
    setBusy(true);
    try {
      await setPriceOverride(listing.id, priceDate, Number(priceValue));
      setPriceDate("");
      setPriceValue("");
      refreshAvailability();
    } catch (err) {
      setError(err.message || "Could not set that price.");
    } finally {
      setBusy(false);
    }
  }

  async function handleDeactivate() {
    if (!window.confirm("Deactivate this listing? It will no longer be bookable.")) return;
    setBusy(true);
    try {
      await deactivateListing(listing.id);
      onDeactivated();
    } catch (err) {
      setError(err.message || "Could not deactivate this listing.");
      setBusy(false);
    }
  }

  return (
    <div className="rounded-2xl border border-brand-beige/40 bg-white p-6 shadow-sm space-y-6">
      <div>
        <p className="text-xs font-medium uppercase tracking-wide text-brand-tan mb-2">You own this listing</p>
        <div className="flex flex-wrap gap-2">
          <Button variant="secondary" to={`/host/listings/${listing.id}/edit`}>Edit listing</Button>
          <Button variant="danger" onClick={handleDeactivate} disabled={busy}>Deactivate</Button>
        </div>
      </div>

      <div className="pt-4 border-t border-brand-beige/40">
        <p className="text-sm font-medium text-brand-dark mb-2">Block a date</p>
        <form onSubmit={handleBlock} className="flex gap-2">
          <input
            type="date"
            value={blockInput}
            onChange={(e) => setBlockInput(e.target.value)}
            className="flex-1 text-sm border border-brand-beige/60 rounded-lg px-3 py-2 outline-none"
            required
          />
          <Button type="submit" variant="secondary" disabled={busy}>Block</Button>
        </form>
      </div>

      <div className="pt-4 border-t border-brand-beige/40">
        <p className="text-sm font-medium text-brand-dark mb-2">Set a custom price</p>
        <form onSubmit={handlePriceOverride} className="flex gap-2">
          <input
            type="date"
            value={priceDate}
            onChange={(e) => setPriceDate(e.target.value)}
            className="flex-1 text-sm border border-brand-beige/60 rounded-lg px-3 py-2 outline-none"
            required
          />
          <input
            type="number"
            step="0.01"
            min="0"
            placeholder="$"
            value={priceValue}
            onChange={(e) => setPriceValue(e.target.value)}
            className="w-20 text-sm border border-brand-beige/60 rounded-lg px-3 py-2 outline-none"
            required
          />
          <Button type="submit" variant="secondary" disabled={busy}>Set</Button>
        </form>
      </div>

      {error && <p className="text-sm text-red-600">{error}</p>}

      {availability && (
        <div className="pt-4 border-t border-brand-beige/40 text-sm">
          <p className="text-brand-tan mb-1">Next 60 days</p>
          <p className="text-brand-dark">
            {availability.blockedDates.length} date{availability.blockedDates.length === 1 ? "" : "s"} blocked,{" "}
            {Object.keys(availability.priceOverrides).length} custom price
            {Object.keys(availability.priceOverrides).length === 1 ? "" : "s"} set
          </p>
        </div>
      )}
    </div>
  );
}

export default HostToolsPanel;
