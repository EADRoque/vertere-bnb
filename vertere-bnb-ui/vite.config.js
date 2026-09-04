import { defineConfig } from "vite";
import react from "@vitejs/plugin-react"
import tailwindcss from "@tailwindcss/vite";

export default defineConfig({
  plugins: [react(), tailwindcss()],
  // GitHub Pages serves this as a project site at
  // https://<username>.github.io/vertere-bnb/, not the domain root, so
  // every asset URL Vite generates needs this prefix or they'll 404.
  base: "/vertere-bnb/",
})