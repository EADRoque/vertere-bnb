// client.js
// This is the one place every API call in the app funnels through. It
// takes care of things every request needs - pointing at the api-gateway,
// attaching the login token, and turning error responses into thrown
// errors - so the rest of the app doesn't have to repeat that logic.

const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api" //the api-gateway's address; every request goes through here, not straight to a microservice. Set VITE_API_BASE_URL at build time to point at the deployed gateway; defaults to the local dev gateway.

export async function apiRequest(path, options = {}) {
  const token = localStorage.getItem("token") //the JWT saved after a successful login, if any

  const headers = {
    "Content-Type": "application/json",
    ...options.headers, //lets a specific call override/add headers if needed
  }

  if (token) {
    headers["Authorization"] = `Bearer ${token}` //attaches the login token so the backend knows who's calling
  }

  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers,
  })

  if (!response.ok) {
    const errorText = await response.text()
    throw new Error(errorText || `Request failed with status ${response.status}`) //turns a failed HTTP response into a JS error the caller can catch
  }

  if (response.status === 204) {
    return null //204 No Content means success but nothing to parse (e.g. a DELETE)
  }

  const contentType = response.headers.get("content-type") || ""
  if (contentType.includes("application/json")) {
    return response.json() //most responses are JSON objects/records
  }
  return response.text() //some endpoints (like login) just return a raw string, e.g. the JWT itself
}
