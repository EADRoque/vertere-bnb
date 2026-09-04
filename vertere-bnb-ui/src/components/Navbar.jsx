// Navbar.jsx
// The top bar shown on every page. Logged out: Log in / Sign up. Logged
// in: links to the trips/messages/notifications/host pages, the user's
// name, and a logout button. The notification bell shows an unread
// count fetched once on login (not real-time - there's no push channel
// from the backend, just a REST poll).

import { useEffect, useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { getMyNotifications } from "../api/notifications";

function NavItem({ to, children }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        `text-sm px-3 py-2 rounded-full transition-colors duration-150 ${
          isActive ? "bg-brand-beige/25 text-brand-dark" : "text-brand-dark/80 hover:bg-brand-beige/15"
        }`
      }
    >
      {children}
    </NavLink>
  );
}

function Navbar() {
  const { user, loading, logout } = useAuth();
  const navigate = useNavigate();
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    if (!user) return;
    getMyNotifications()
      .then((list) => setUnreadCount(list.filter((n) => !n.read).length))
      .catch(() => {});
  }, [user]);

  function handleLogout() {
    logout();
    navigate("/");
  }

  return (
    <header className="border-b border-brand-beige/40 bg-white sticky top-0 z-10">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
        <Link to="/" className="text-xl font-semibold text-brand-dark tracking-tight shrink-0">
          Vertere<span className="text-brand-terracotta">BnB</span>
        </Link>

        {loading ? null : user ? (
          <nav className="flex items-center gap-1">
            <NavItem to="/listings">Browse</NavItem>
            <NavItem to="/trips">Trips</NavItem>
            <NavItem to="/messages">Messages</NavItem>
            <NavItem to="/notifications">
              Notifications
              {unreadCount > 0 && (
                <span className="ml-1.5 inline-flex items-center justify-center w-4 h-4 text-[10px] rounded-full bg-brand-terracotta text-white align-middle">
                  {unreadCount}
                </span>
              )}
            </NavItem>
            <NavItem to="/host/listings">Host</NavItem>

            <span className="mx-2 h-5 w-px bg-brand-beige/40" />

            <span className="text-sm text-brand-dark hidden sm:inline pr-1">
              {user.fullName?.split(" ")[0] || "there"}
            </span>
            <button
              onClick={handleLogout}
              className="text-sm px-4 py-2 rounded-full border border-brand-tan text-brand-dark hover:bg-brand-beige/20 transition-colors duration-150"
            >
              Log out
            </button>
          </nav>
        ) : (
          <nav className="flex items-center gap-2">
            <NavItem to="/listings">Browse</NavItem>
            <Link
              to="/login"
              className="text-sm px-4 py-2 rounded-full text-brand-dark hover:bg-brand-beige/20 transition-colors duration-150"
            >
              Log in
            </Link>
            <Link
              to="/register"
              className="text-sm px-4 py-2 rounded-full bg-brand-terracotta text-white hover:bg-brand-sand transition-colors duration-150"
            >
              Sign up
            </Link>
          </nav>
        )}
      </div>
    </header>
  );
}

export default Navbar;
