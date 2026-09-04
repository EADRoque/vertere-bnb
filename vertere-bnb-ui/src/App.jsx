// App.jsx
// This defines which page component shows for which URL. Every route is
// nested under Layout, which renders the shared Navbar around whichever
// page matched.

import { BrowserRouter, Routes, Route } from "react-router-dom"; //client-side routing, so navigating doesn't reload the whole page
import Layout from "./components/Layout";
import RequireAuth from "./components/RequireAuth";
import HomePage from "./pages/HomePage";
import ListingsPage from "./pages/ListingsPage";
import ListingDetailPage from "./pages/ListingDetailPage";
import ReviewFormPage from "./pages/ReviewFormPage";
import BookingsPage from "./pages/BookingsPage";
import MessagesPage from "./pages/MessagesPage";
import NotificationsPage from "./pages/NotificationsPage";
import MyListingsPage from "./pages/MyListingsPage";
import ListingFormPage from "./pages/ListingFormPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";

function App() {
  return (
    // basename matches vite.config.js's `base` - keeps every generated link under /vertere-bnb/ instead of the domain root
    <BrowserRouter basename={import.meta.env.BASE_URL}>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<HomePage />} />
          <Route path="/listings" element={<ListingsPage />} />
          <Route path="/listings/:id" element={<ListingDetailPage />} />

          <Route element={<RequireAuth />}>
            <Route path="/listings/:id/review" element={<ReviewFormPage />} />
            <Route path="/trips" element={<BookingsPage />} />
            <Route path="/messages" element={<MessagesPage />} />
            <Route path="/notifications" element={<NotificationsPage />} />
            <Route path="/host/listings" element={<MyListingsPage />} />
            <Route path="/host/listings/new" element={<ListingFormPage />} />
            <Route path="/host/listings/:id/edit" element={<ListingFormPage />} />
          </Route>

          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );  
}

export default App
