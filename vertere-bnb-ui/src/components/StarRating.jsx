// StarRating.jsx
// Shows a 1-5 star rating. Read-only by default (for displaying an
// existing review); pass `onChange` to make it an interactive picker
// (for writing a new review).

function StarRating({ value = 0, onChange, size = "text-base" }) {
  const stars = [1, 2, 3, 4, 5];
  const interactive = typeof onChange === "function";

  return (
    <div className={`inline-flex gap-0.5 ${size}`} role={interactive ? "radiogroup" : undefined}>
      {stars.map((star) => (
        <span
          key={star}
          onClick={interactive ? () => onChange(star) : undefined}
          className={
            star <= value
              ? "text-brand-terracotta"
              : "text-brand-beige/60"
          }
          style={interactive ? { cursor: "pointer" } : undefined}
        >
          ★
        </span>
      ))}
    </div>
  );
}

export default StarRating;
