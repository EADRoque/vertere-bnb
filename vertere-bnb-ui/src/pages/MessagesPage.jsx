// MessagesPage.jsx
// A two-pane inbox: conversation list on the left, the selected thread
// on the right. There's no endpoint to fetch a single conversation or
// to look up another user's name, so this page works entirely off
// getMyConversations() (which nests every message already) and labels
// the other participant by their role ("Host"/"Guest") rather than by
// name.

import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { getMyConversations, sendMessage } from "../api/messaging";
import { getListing } from "../api/listings";
import { useAuth } from "../context/AuthContext";
import Spinner from "../components/Spinner";
import EmptyState from "../components/EmptyState";
import Button from "../components/Button";

function MessagesPage() {
  const { user } = useAuth();
  const [searchParams, setSearchParams] = useSearchParams();
  const [conversations, setConversations] = useState(null);
  const [listingsById, setListingsById] = useState({});
  const [loading, setLoading] = useState(true);
  const [draft, setDraft] = useState("");
  const [sending, setSending] = useState(false);

  const selectedId = searchParams.get("id");
  const selected = conversations?.find((c) => c.id === selectedId) || conversations?.[0];

  useEffect(() => {
    let cancelled = false;

    getMyConversations()
      .then(async (data) => {
        if (cancelled) return;
        setConversations(data);

        const uniqueListingIds = [...new Set(data.map((c) => c.listingId))];
        const results = await Promise.all(
          uniqueListingIds.map((id) => getListing(id).catch(() => null))
        );
        if (cancelled) return;

        const map = {};
        uniqueListingIds.forEach((id, i) => {
          if (results[i]) map[id] = results[i];
        });
        setListingsById(map);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, []);

  async function handleSend(e) {
    e.preventDefault();
    if (!selected || !draft.trim()) return;

    setSending(true);
    try {
      const message = await sendMessage(selected.id, draft.trim());
      setConversations((prev) =>
        prev.map((c) => (c.id === selected.id ? { ...c, messages: [...c.messages, message] } : c))
      );
      setDraft("");
    } finally {
      setSending(false);
    }
  }

  if (loading) return <Spinner label="Loading messages..." />;

  if (conversations.length === 0) {
    return (
      <div className="max-w-4xl mx-auto px-4 sm:px-6 py-10">
        <EmptyState
          title="No conversations yet"
          description="Message a host from a listing page to start one."
          action={<Button to="/listings">Browse stays</Button>}
        />
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 py-10">
      <h1 className="text-2xl font-semibold text-brand-dark mb-6">Messages</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 rounded-2xl border border-brand-beige/40 bg-white shadow-sm overflow-hidden min-h-[28rem]">
        <div className="border-b md:border-b-0 md:border-r border-brand-beige/40 divide-y divide-brand-beige/30">
          {conversations.map((c) => {
            const listing = listingsById[c.listingId];
            const lastMessage = c.messages[c.messages.length - 1];
            const isSelected = selected?.id === c.id;

            return (
              <button
                key={c.id}
                onClick={() => setSearchParams({ id: c.id })}
                className={`w-full text-left px-4 py-3 transition-colors duration-150 ${
                  isSelected ? "bg-brand-beige/20" : "hover:bg-brand-beige/10"
                }`}
              >
                <p className="text-sm font-medium text-brand-dark truncate">
                  {listing ? listing.title : "Listing"}
                </p>
                <p className="text-xs text-brand-tan truncate mt-0.5">
                  {lastMessage ? lastMessage.body : "No messages yet"}
                </p>
              </button>
            );
          })}
        </div>

        <div className="md:col-span-2 flex flex-col">
          {selected && (
            <>
              <div className="px-5 py-3 border-b border-brand-beige/40">
                <p className="text-sm font-medium text-brand-dark">
                  {listingsById[selected.listingId]?.title || "Conversation"}
                </p>
              </div>

              <div className="flex-1 px-5 py-4 space-y-3 overflow-y-auto max-h-96">
                {selected.messages.map((m) => {
                  const mine = m.senderUserId === user.id;
                  return (
                    <div key={m.id} className={`flex ${mine ? "justify-end" : "justify-start"}`}>
                      <div
                        className={`max-w-xs px-4 py-2 rounded-2xl text-sm ${
                          mine
                            ? "bg-brand-terracotta text-white rounded-br-sm"
                            : "bg-brand-beige/20 text-brand-dark rounded-bl-sm"
                        }`}
                      >
                        {m.body}
                      </div>
                    </div>
                  );
                })}
              </div>

              <form onSubmit={handleSend} className="p-4 border-t border-brand-beige/40 flex gap-2">
                <input
                  type="text"
                  placeholder="Write a message..."
                  value={draft}
                  onChange={(e) => setDraft(e.target.value)}
                  className="flex-1 text-sm border border-brand-beige/60 rounded-full px-4 py-2 outline-none focus:ring-2 focus:ring-brand-terracotta/40"
                />
                <Button type="submit" disabled={sending || !draft.trim()}>
                  Send
                </Button>
              </form>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default MessagesPage;
