import Link from "next/link";
import type { MatchDiscussion } from "@/lib/types";

export function DiscussionSidebar({ discussions }: { discussions: MatchDiscussion[] }) {
  return (
    <aside className="w-[300px] shrink-0 border-l border-line pl-8">
      <h2 className="text-lg font-bold text-ink">오늘의 토론장</h2>
      {discussions.length === 0 ? (
        <p className="mt-4 text-sm text-muted">오늘 예정된 경기가 없습니다.</p>
      ) : (
        <ul className="mt-4 flex flex-col">
          {discussions.map(({ match, commentCount }, i) => (
            <li
              key={match.id}
              className={i > 0 ? "border-t border-line pt-4 mt-4" : undefined}
            >
              <Link href={`/match/${match.id}`} className="block">
                <p className="font-serif text-sm font-bold leading-snug text-ink">
                  {match.homeTeam.name} vs {match.awayTeam.name}
                </p>
                <p className="mt-2 text-xs text-muted">
                  {match.kickoffTime ?? match.status} · 댓글 {commentCount}
                </p>
              </Link>
            </li>
          ))}
        </ul>
      )}
    </aside>
  );
}
