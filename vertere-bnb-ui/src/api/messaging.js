// messaging.js
// API calls for guest/host conversations. There's no "get one
// conversation" endpoint - getMyConversations returns every
// conversation the user is part of, each with its full message list
// already nested inside, so that's used both for the inbox list and for
// rendering a single open thread.

import { apiRequest } from "./client";

export function startConversation(data) {
  // data: { listingId, hostUserId, firstMessage }
  return apiRequest("/conversations", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export function sendMessage(conversationId, body) {
  return apiRequest(`/conversations/${conversationId}/messages`, {
    method: "POST",
    body: JSON.stringify({ body }),
  });
}

export function getMyConversations() {
  return apiRequest("/conversations/mine");
}
