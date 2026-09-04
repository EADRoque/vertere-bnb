// Layout.jsx
// Wraps every page with the shared Navbar. React Router renders the
// matched page in place of <Outlet /> below.

import { Outlet } from "react-router-dom";
import Navbar from "./Navbar";

function Layout() {
  return (
    <div className="min-h-screen flex flex-col bg-[#FAF7F2]">
      <Navbar />
      <main className="flex-1">
        <Outlet />
      </main>
    </div>
  );
}

export default Layout;
