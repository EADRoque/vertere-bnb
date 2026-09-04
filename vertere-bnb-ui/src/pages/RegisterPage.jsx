// RegisterPage.jsx
// The sign-up form - collects name/email/password, asks AuthContext to
// register (and then log in) the user, and redirects to the home page
// on success.

import { useState } from "react";
import { useNavigate, Link } from "react-router-dom"; //useNavigate redirects after registering; Link goes to the login page without a full reload
import { useAuth } from "../context/AuthContext"; //gives access to the shared register() function

function RegisterPage() {
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(""); //holds a message to show if registration fails
  const { register } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault(); //stop the browser from doing a full-page form submit
    setError("");
    try {
      await register(email, password, fullName);
      navigate("/"); //go to the home page once registered and logged in
    } catch {
      setError("Could not register. Email may already be in use."); //most likely cause, matching the backend's duplicate-email check
    }
  }

  return (
    <div className="max-w-md mx-auto mt-20 mb-20 p-8 bg-white border border-brand-beige/40 rounded-2xl shadow-sm">
      <h1 className="text-2xl font-semibold text-brand-dark mb-6">Create an account</h1>

      {error && <p className="text-red-600 mb-4 text-sm">{error}</p>}

      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          type="text"
          placeholder="Full name"
          value={fullName}
          onChange={(e) => setFullName(e.target.value)}
          className="w-full border border-brand-beige/60 rounded-lg px-3 py-2 text-brand-dark placeholder:text-brand-beige focus:outline-none focus:ring-2 focus:ring-brand-terracotta/40 transition-colors duration-150"
          required
        />
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="w-full border border-brand-beige/60 rounded-lg px-3 py-2 text-brand-dark placeholder:text-brand-beige focus:outline-none focus:ring-2 focus:ring-brand-terracotta/40 transition-colors duration-150"
          required
        />
        <input
          type="password"
          placeholder="Password (min. 8 characters)"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="w-full border border-brand-beige/60 rounded-lg px-3 py-2 text-brand-dark placeholder:text-brand-beige focus:outline-none focus:ring-2 focus:ring-brand-terracotta/40 transition-colors duration-150"
          minLength={8}
          required
        />
        <button
          type="submit"
          className="w-full bg-brand-terracotta text-white rounded-lg px-3 py-2 hover:bg-brand-sand transition-colors duration-150"
        >
          Register
        </button>
      </form>

      <p className="mt-4 text-sm text-brand-tan">
        Already have an account? <Link to="/login" className="text-brand-terracotta hover:text-brand-sand">Log in</Link>
      </p>
    </div>
  );
}

export default RegisterPage;