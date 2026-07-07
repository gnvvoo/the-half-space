import type { Discussion } from "@/lib/types";

export function DiscussionSidebar({ discussions }: { discussions: Discussion[] }) {
  return (
    <aside className="w-[300px] shrink-0 border-l border-line pl-8">
      <h2 className="text-lg font-bold text-ink">인기 토론</h2>
      <ul className="mt-4 flex flex-col">
        {discussions.map((d, i) => (
          <li
            key={d.id}
            className={i > 0 ? "border-t border-line pt-4 mt-4" : undefined}
          >
            <p className="font-serif text-sm font-bold leading-snug text-ink">
              {d.title}
            </p>
            <p className="mt-2 text-xs text-muted">
              좋아요 {d.likes} · 댓글 {d.commentCount}
            </p>
          </li>
        ))}
      </ul>
    </aside>
  );
}
