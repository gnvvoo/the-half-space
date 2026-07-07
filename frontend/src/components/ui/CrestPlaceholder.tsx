import { cx } from "@/lib/utils";

const SIZE_CLASSES = {
  sm: "h-6 w-6 text-[9px]",
  md: "h-10 w-10 text-[11px]",
  lg: "h-16 w-16 text-base",
  xl: "h-[88px] w-[88px] text-lg",
} as const;

export function CrestPlaceholder({
  code,
  size = "sm",
  className,
}: {
  code: string;
  size?: keyof typeof SIZE_CLASSES;
  className?: string;
}) {
  return (
    <span
      className={cx(
        "flex shrink-0 items-center justify-center rounded-full border border-line bg-[#F3F2EF] font-bold text-ink/70",
        SIZE_CLASSES[size],
        className
      )}
      aria-hidden
    >
      {code}
    </span>
  );
}
