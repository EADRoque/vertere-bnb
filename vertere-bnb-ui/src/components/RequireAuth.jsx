// RequireAuth.jsx
// Wraps a route that needs a logged-in user. Shows a spinner while the
// initial session check is still running, then either renders the page
// or bounces to /login (remembering where the visitor was headed).

import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import Spinner from "./Spinner";

function RequireAuth() {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading) return <Spinner />;
  if (!user) return <Navigate to="/login" state={{ from: location }} replace />;

  return <Outlet />;
}

export default RequireAuth;
