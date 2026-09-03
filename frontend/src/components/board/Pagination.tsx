import Link from "next/link";
import { cx } from "@/lib/utils";

export function Pagination({
  boardCode,
  page,
  totalElements,
  size,
  title,
}: {
  boardCode: string;
  page: number;
  totalElements: number;
  size: number;
  title?: string;
}) {
  const totalPages = Math.max(1, Math.ceil(totalElements / size));
  if (totalPages <= 1) return null;

  const href = (p: number) => {
    const params = new URLSearchParams({ page: String(p) });
    if (title) params.set("title", title);
    return `/boards/${boardCode}?${params}`;
  };

  return (
    <div className="mt-6 flex justify-center gap-2">
      {Array.from({ length: totalPages }, (_, i) => i).map((p) => (
        <Link
          key={p}
          href={href(p)}
          className={cx(
            "flex h-8 w-8 items-center justify-center border text-xs font-semibold",
            p === page ? "border-ink bg-ink text-white" : "border-line text-ink/70 hover:border-ink"
          )}
        >
          {p + 1}
        </Link>
      ))}
    </div>
  );
}
