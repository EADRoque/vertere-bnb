// LoginPage.jsx
// The login form - collects email/password, asks AuthContext to log the
// user in, and redirects to the home page on success.

import { useState } from "react";
import { useNavigate, Link } from "react-router-dom"; //useNavigate redirects after login; Link goes to the register page without a full reload
import { useAuth } from "../context/AuthContext"; //gives access to the shared login() function

function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(""); //holds a message to show if login fails
    const { login } = useAuth();
    const navigate = useNavigate();

    async function handleSubmit(e) {
        e.preventDefault(); //stop the browser from doing a full-page form submit
        setError("");
        try {
            await login(email, password);
            navigate("/"); //go to the home page once logged in
        } catch {
            setError("Invalid email or password"); //deliberately vague, matching the backend's generic error
        }
    }

    return (
        <div className="max-w-md mx-auto mt-20 mb-20 p-8 bg-white border border-brand-beige/40 rounded-2xl shadow-sm">
            <h1 className="text-2xl font-semibold text-brand-dark mb-6">Log in</h1>

            {error && <p className="text-red-600 mb-4 text-sm">{error}</p>}

            <form onSubmit={handleSubmit} className="space-y-4">
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
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full border border-brand-beige/60 rounded-lg px-3 py-2 text-brand-dark placeholder:text-brand-beige focus:outline-none focus:ring-2 focus:ring-brand-terracotta/40 transition-colors duration-150"
                    required
                />
                <button
                    type="submit"
                    className="w-full bg-brand-terracotta text-white rounded-lg px-3 py-2 hover:bg-brand-sand transition-colors duration-150"
                >
                    Log in
                </button>
            </form>

            <p className="mt-4 text-sm text-brand-tan">
                Don't have an account? <Link to="/register" className="text-brand-terracotta hover:text-brand-sand">Register</Link>
            </p>
        </div>
    );
}

export default LoginPage;