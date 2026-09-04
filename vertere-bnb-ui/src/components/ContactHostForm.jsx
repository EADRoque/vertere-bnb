// ContactHostForm.jsx
// A small expandable "message the host" box shown on a listing's detail
// page. Starts a new conversation and hands the guest off to the
// messages inbox to continue it.

import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { startConversation } from "../api/messaging";
import { useAuth } from "../context/AuthContext";
import Button from "./Button";

function ContactHostForm({ listing }) {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  async function handleSend(e) {
    e.preventDefault();
    if (!user) {
      navigate("/login");
      return;
    }

    setSubmitting(true);
    setError("");
    try {
      await startConversation({
        listingId: listing.id,
        hostUserId: listing.hostUserId,
        firstMessage: message,
      });
      navigate("/messages");
    } catch (err) {
      setError(err.message || "Could not send your message.");
    } finally {
      setSubmitting(false);
    }
  }

  if (!open) {
    return (
      <Button variant="secondary" onClick={() => setOpen(true)}>
        Message the host
      </Button>
    );
  }

  return (
    <form onSubmit={handleSend} className="mt-4 p-4 rounded-xl border border-brand-beige/50 bg-white space-y-3">
      <textarea
        placeholder="Ask a question about this place..."
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        rows={3}
        className="w-full text-sm border border-brand-beige/60 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-brand-terracotta/40 resize-none"
        required
      />
      {error && <p className="text-sm text-red-600">{error}</p>}
      <div className="flex gap-2">
        <Button type="submit" disabled={submitting}>
          {submitting ? "Sending..." : "Send message"}
        </Button>
        <Button type="button" variant="ghost" onClick={() => setOpen(false)}>
          Cancel
        </Button>
      </div>
    </form>
  );
}

export default ContactHostForm;
