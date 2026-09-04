// NotificationsPage.jsx
// The current user's notification list. `type` and `payloadJson` are
// freeform strings on the backend (no fixed schema per type yet), so
// this renders `type` as a label and pretty-prints whatever JSON is in
// payloadJson underneath it, falling back to the raw text if it isn't
// valid JSON.

import { useEffect, useState } from "react";
import { getMyNotifications, markNotificationAsRead } from "../api/notifications";
import Spinner from "../components/Spinner";
import EmptyState from "../components/EmptyState";

function formatPayload(payloadJson) {
  try {
    const parsed = JSON.parse(payloadJson);
    return Object.entries(parsed)
      .map(([key, value]) => `${key}: ${value}`)
      .join(" · ");
  } catch {
    return payloadJson;
  }
}

function formatType(type) {
  return type
    .toLowerCase()
    .split("_")
    .map((word) => word[0].toUpperCase() + word.slice(1))
    .join(" ");
}

function NotificationsPage() {
  const [notifications, setNotifications] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getMyNotifications()
      .then(setNotifications)
      .finally(() => setLoading(false));
  }, []);

  async function handleClick(notification) {
    if (notification.read) return;
    setNotifications((prev) =>
      prev.map((n) => (n.id === notification.id ? { ...n, read: true } : n))
    );
    try {
      await markNotificationAsRead(notification.id);
    } catch {
      // revert on failure
      setNotifications((prev) =>
        prev.map((n) => (n.id === notification.id ? { ...n, read: false } : n))
      );
    }
  }

  if (loading) return <Spinner label="Loading notifications..." />;

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 py-10">
      <h1 className="text-2xl font-semibold text-brand-dark mb-8">Notifications</h1>

      {notifications.length === 0 ? (
        <EmptyState title="You're all caught up" description="New notifications will show up here." />
      ) : (
        <div className="space-y-3">
          {notifications.map((n) => (
            <button
              key={n.id}
              onClick={() => handleClick(n)}
              className={`w-full text-left p-4 rounded-xl border transition-colors duration-150 ${
                n.read
                  ? "bg-white border-brand-beige/30"
                  : "bg-brand-beige/10 border-brand-terracotta/30"
              }`}
            >
              <div className="flex items-start gap-3">
                {!n.read && <span className="w-2 h-2 mt-1.5 rounded-full bg-brand-terracotta shrink-0" />}
                <div className="min-w-0">
                  <p className="text-sm font-medium text-brand-dark">{formatType(n.type)}</p>
                  <p className="text-sm text-brand-tan mt-0.5 truncate">{formatPayload(n.payloadJson)}</p>
                </div>
              </div>
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

export default NotificationsPage;
