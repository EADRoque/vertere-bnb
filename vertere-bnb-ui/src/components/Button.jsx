// Button.jsx
// A small shared button so every primary/secondary/danger action in the
// app looks and behaves the same, instead of re-typing the same Tailwind
// classes on every page. Renders a <Link> when `to` is given, otherwise
// a plain <button>.

import { Link } from "react-router-dom";

const VARIANTS = {
  primary:
    "bg-brand-terracotta text-white hover:bg-brand-sand",
  secondary:
    "border border-brand-tan text-brand-dark hover:bg-brand-beige/20",
  ghost:
    "text-brand-dark hover:bg-brand-beige/20",
  danger:
    "border border-red-300 text-red-600 hover:bg-red-50",
};

function Button({ variant = "primary", to, className = "", children, ...props }) {
  const classes = `inline-flex items-center justify-center gap-2 rounded-full px-5 py-2.5 text-sm font-medium transition-colors duration-150 disabled:opacity-40 disabled:cursor-not-allowed ${VARIANTS[variant]} ${className}`;

  if (to) {
    return (
      <Link to={to} className={classes} {...props}>
        {children}
      </Link>
    );
  }

  return (
    <button className={classes} {...props}>
      {children}
    </button>
  );
}

export default Button;
