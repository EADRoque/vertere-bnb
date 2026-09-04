// Spinner.jsx
// A small inline loading indicator, used whenever a page is waiting on
// an API call.

function Spinner({ label = "Loading..." }) {
  return (
    <div className="flex items-center justify-center gap-3 py-16 text-brand-tan">
      <span className="w-5 h-5 rounded-full border-2 border-brand-beige border-t-brand-terracotta animate-spin" />
      <span className="text-sm">{label}</span>
    </div>
  );
}

export default Spinner;
