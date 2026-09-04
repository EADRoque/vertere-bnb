// notifications.js
// API calls for the current user's notifications. Notifications are
// created by the backend itself (e.g. when a booking is confirmed) -
// the frontend only ever reads and marks them as read.

import { apiRequest } from "./client";

export function getMyNotifications() {
  return apiRequest("/notifications/mine");
}

export function markNotificationAsRead(id) {
  return apiRequest(`/notifications/${id}/read`, { method: "PUT" });
}
