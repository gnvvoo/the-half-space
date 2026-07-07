import type { FormResult } from "@/lib/types";
import { cx } from "@/lib/utils";

const STYLES: Record<FormResult, string> = {
  W: "bg-success text-white",
  D: "bg-[#9CA3AF] text-white",
  L: "bg-brand text-white",
};

export function FormBadge({ result }: { result: FormResult }) {
  return (
    <span
      className={cx(
        "flex h-5 w-5 items-center justify-center text-[10px] font-bold",
        STYLES[result]
      )}
    >
      {result}
    </span>
  );
}

export function FormRow({ results }: { results: FormResult[] }) {
  return (
    <div className="flex gap-1">
      {results.map((r, i) => (
        <FormBadge key={i} result={r} />
      ))}
    </div>
  );
}
