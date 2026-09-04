// StatusBadge.jsx
// A small colored pill for a booking's status. Kept separate from the
// warm brand palette on purpose - status needs to read instantly as
// good/bad/pending, which a monochrome palette can't do as clearly.

const STYLES = {
  CONFIRMED: "bg-emerald-50 text-emerald-700 border-emerald-200",
  PENDING: "bg-amber-50 text-amber-700 border-amber-200",
  CANCELLED: "bg-rose-50 text-rose-700 border-rose-200",
};

function StatusBadge({ status }) {
  const style = STYLES[status] || "bg-brand-beige/20 text-brand-dark border-brand-beige/40";

  return (
    <span className={`inline-block text-xs font-medium px-2.5 py-1 rounded-full border ${style}`}>
      {status}
    </span>
  );
}

export default StatusBadge;
