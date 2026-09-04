// listings.js
// API calls for browsing, viewing, and (as a host) managing listings.

import { apiRequest } from "./client";

export function searchListings(params = {}) {
  // params can include: city, checkIn, checkOut, minGuests, minPrice, maxPrice, page, size
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      query.set(key, value);
    }
  });

  const queryString = query.toString();
  return apiRequest(`/listings/search${queryString ? `?${queryString}` : ""}`);
  // resolves to a Spring Page object: { content: [...], totalElements, totalPages, number, size, ... }
}

export function getListing(id) {
  return apiRequest(`/listings/${id}`);
}

export function createListing(data) {
  return apiRequest("/listings", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export function updateListing(id, data) {
  return apiRequest(`/listings/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export function deactivateListing(id) {
  return apiRequest(`/listings/${id}`, { method: "DELETE" });
}

export function getAvailability(id, start, end) {
  return apiRequest(`/listings/${id}/availability?start=${start}&end=${end}`);
  // resolves to { blockedDates: [...], priceOverrides: { "2026-09-10": 120.0, ... } }
}

export function blockDate(id, date) {
  return apiRequest(`/listings/${id}/availability/block`, {
    method: "PUT",
    body: JSON.stringify({ date }),
  });
}

export function setPriceOverride(id, date, price) {
  return apiRequest(`/listings/${id}/availability/price`, {
    method: "PUT",
    body: JSON.stringify({ date, price }),
  });
}
