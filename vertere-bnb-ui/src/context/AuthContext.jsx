// AuthContext.jsx
// This holds the app's shared "who is logged in" state, so any component
// can ask who the current user is (or trigger login/register/logout)
// without passing that info down through every layer of props.

import { createContext, useContext, useState, useEffect } from "react";
import { loginUser, registerUser, getCurrentUser } from "../api/auth";

const AuthContext = createContext(null); //the shared "box" that holds the auth state; starts empty until AuthProvider fills it in

export function AuthProvider({ children }) { //wraps the whole app (see main.jsx) so every page can read auth state
    const [user, setUser] = useState(null); //the logged-in user's profile, or null if nobody's logged in
    const [loading, setLoading] = useState(true); //true while we're still checking for a saved login on startup

    useEffect(() => {
        const token = localStorage.getItem("token"); //check if the browser already has a saved login from a previous visit
        if (!token) {
            setLoading(false); //nothing to restore - stop showing a loading state
            return;
        }

        getCurrentUser()
        .then(setUser) //restore the logged-in user so a page reload doesn't look logged-out
        .catch(() => localStorage.removeItem("token")) //the saved token is invalid/expired - clear it out
        .finally(() => setLoading(false));
    }, []); //runs once, when the app first loads

    async function login(email, password) {
        const token = await loginUser({ email, password });
        localStorage.setItem("token", token); //save the token so future requests (and future visits) stay logged in
        const currentUser = await getCurrentUser(); //fetch the profile now that we're authenticated
        setUser(currentUser);
    }

    async function register(email, password, fullName) {
        await registerUser({ email, password, fullName });
        await login(email, password); //automatically log the new user in right after signing up
    }

    function logout() {
        localStorage.removeItem("token");
        setUser(null);
    }

    return (
        <AuthContext.Provider value={{ user, loading, login, register, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

// eslint-disable-next-line react-refresh/only-export-components -- useAuth is tightly coupled to AuthContext/AuthProvider; splitting it into its own file for Fast Refresh isn't worth the indirection here
export function useAuth() { //the hook components call to read/use the auth state (e.g. useAuth().login(...))
    return useContext(AuthContext);
}
