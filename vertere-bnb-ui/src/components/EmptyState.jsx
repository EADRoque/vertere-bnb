// EmptyState.jsx
// A shared "nothing here yet" placeholder, used wherever a list could
// legitimately be empty (no bookings, no messages, no notifications...).

function EmptyState({ title, description, action }) {
  return (
    <div className="text-center py-16 px-4 border border-dashed border-brand-beige/50 rounded-2xl bg-white/50">
      <p className="text-brand-dark font-medium">{title}</p>
      {description && <p className="text-brand-tan text-sm mt-1">{description}</p>}
      {action && <div className="mt-6">{action}</div>}
    </div>
  );
}

export default EmptyState;
