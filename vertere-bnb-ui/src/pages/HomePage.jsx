// HomePage.jsx
// The landing page: a hero with a city search bar, a row of popular
// destinations (quick searches), a short "how it works" explainer, and
// a closing CTA inviting visitors to list their own place.

import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import Button from "../components/Button";

const POPULAR_DESTINATIONS = ["Manila", "Cebu", "Tokyo", "Bali", "Seoul", "Bangkok"];

const STEPS = [
  {
    title: "Search",
    description: "Tell us where you're headed and browse real places to stay.",
  },
  {
    title: "Book",
    description: "Pick your dates and reserve instantly - no back and forth.",
  },
  {
    title: "Stay",
    description: "Message your host, check in, and enjoy the trip.",
  },
];

function HomePage() {
  const [city, setCity] = useState("");
  const navigate = useNavigate();
  const { user } = useAuth();

  function goToCity(value) {
    const params = new URLSearchParams();
    if (value.trim()) params.set("city", value.trim());
    navigate(`/listings?${params.toString()}`);
  }

  function handleSearch(e) {
    e.preventDefault();
    goToCity(city);
  }

  return (
    <div>
      {/* hero */}
      <section className="relative overflow-hidden">
        <div className="absolute inset-0 bg-linear-to-br from-brand-beige/40 via-brand-sand/20 to-transparent" />
        <div className="relative max-w-6xl mx-auto px-4 sm:px-6 pt-20 pb-24 text-center">
          <span className="inline-block text-xs font-medium tracking-wide uppercase text-brand-terracotta bg-brand-terracotta/10 px-3 py-1 rounded-full mb-6">
            Stays for every kind of trip
          </span>
          <h1 className="text-4xl sm:text-5xl font-semibold text-brand-dark tracking-tight leading-tight">
            Find your next stay
          </h1>
          <p className="mt-4 text-brand-tan text-lg max-w-xl mx-auto">
            Comfortable, well-reviewed places to stay, wherever you're headed next.
          </p>

          <form
            onSubmit={handleSearch}
            className="mt-10 max-w-xl mx-auto flex items-center bg-white rounded-full border border-brand-beige/50 shadow-md p-2 gap-2"
          >
            <input
              type="text"
              placeholder="Search by city"
              value={city}
              onChange={(e) => setCity(e.target.value)}
              className="flex-1 px-4 py-2.5 rounded-full outline-none text-brand-dark placeholder:text-brand-beige"
            />
            <Button type="submit">Search</Button>
          </form>

          <div className="mt-6 flex flex-wrap justify-center gap-2">
            {POPULAR_DESTINATIONS.map((destination) => (
              <button
                key={destination}
                onClick={() => goToCity(destination)}
                className="text-sm px-3.5 py-1.5 rounded-full border border-brand-beige/60 text-brand-dark hover:bg-brand-beige/20 transition-colors duration-150"
              >
                {destination}
              </button>
            ))}
          </div>
        </div>
      </section>

      {/* how it works */}
      <section className="max-w-6xl mx-auto px-4 sm:px-6 py-16">
        <h2 className="text-2xl font-semibold text-brand-dark text-center mb-12">How it works</h2>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-8">
          {STEPS.map((step, i) => (
            <div key={step.title} className="text-center">
              <div className="w-10 h-10 mx-auto rounded-full bg-brand-terracotta/10 text-brand-terracotta font-semibold flex items-center justify-center mb-4">
                {i + 1}
              </div>
              <h3 className="font-medium text-brand-dark mb-1">{step.title}</h3>
              <p className="text-sm text-brand-tan max-w-xs mx-auto">{step.description}</p>
            </div>
          ))}
        </div>
      </section>

      {/* host CTA */}
      <section className="bg-brand-dark">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 py-16 text-center">
          <h2 className="text-2xl sm:text-3xl font-semibold text-white">Have a place to share?</h2>
          <p className="mt-3 text-brand-beige max-w-md mx-auto">
            List your space on Vertere BnB and start hosting guests from around the world.
          </p>
          <div className="mt-8">
            <Button to={user ? "/host/listings/new" : "/register"} className="bg-brand-terracotta">
              {user ? "Create a listing" : "Get started"}
            </Button>
          </div>
        </div>
      </section>
    </div>
  );
}

export default HomePage;
