export function cx(...classes: Array<string | false | null | undefined>): string {
  return classes.filter(Boolean).join(" ");
}

export function formatSigned(value: number): string {
  return value > 0 ? `+${value}` : `${value}`;
}

export function formatNumber(value: number): string {
  return value.toLocaleString("ko-KR");
}

/** Shared "active tab = red underline" treatment used across nav/tab groups. */
export function underlineTabClass(active: boolean): string {
  return cx(
    "relative pb-[13px] text-sm font-bold transition-colors",
    active ? "text-brand" : "text-ink/70 hover:text-ink"
  );
}

