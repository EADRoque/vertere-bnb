// auth.js
// These are the specific API calls related to signing up, logging in,
// and fetching the current user - each one just calls apiRequest with
// the right path and body, so the rest of the app doesn't need to know
// the exact endpoint URLs.

import { apiRequest } from "./client"; //the shared helper that actually sends the request and handles errors

export function registerUser(data) { //data is expected to have email, password, fullName
    return apiRequest("/auth/register", {
        method: "POST",
        body: JSON.stringify(data),
    });
}

export function loginUser(data) { //data is expected to have email, password; resolves to the raw JWT string
    return apiRequest("/auth/login", {
        method: "POST",
        body: JSON.stringify(data),
    });
}

export function getCurrentUser() { //looks up the profile for whoever the saved token belongs to
  return apiRequest("/users/me");
}
