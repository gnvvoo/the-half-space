import { MatchCard } from "./MatchCard";
import type { Match } from "@/lib/types";

export function UpcomingMatches({ matches }: { matches: Match[] }) {
  return (
    <section>
      <h2 className="text-lg font-bold text-ink">예정된 경기</h2>
      {matches.length === 0 ? (
        <p className="mt-3 border border-line px-4 py-10 text-center text-sm text-muted">
          예정된 경기가 없습니다.
        </p>
      ) : (
        <div className="mt-3 grid grid-cols-3 gap-[14px]">
          {matches.map((match) => (
            <MatchCard key={match.id} match={match} />
          ))}
        </div>
      )}
    </section>
  );
}
