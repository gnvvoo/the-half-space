export function LiveBadge({ minute }: { minute?: number }) {
  return (
    <span
      className="inline-flex w-fit items-center gap-[6px] px-2 py-[3px] text-[11px] font-bold text-brand"
      style={{ background: "rgba(220,38,38,.22)" }}
    >
      <span className="live-pulse-dot h-[6px] w-[6px] rounded-full bg-brand" />
      LIVE {minute != null ? `${minute}'` : ""}
    </span>
  );
}
