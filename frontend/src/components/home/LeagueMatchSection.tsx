import Link from "next/link";
import { MatchCard } from "./MatchCard";
import type { League, Match } from "@/lib/types";

export function LeagueMatchSection({
  league,
  matches,
}: {
  league: League;
  matches: Match[];
}) {
  if (matches.length === 0) return null;

  return (
    <section id={league.slug} className="scroll-mt-24">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-bold text-ink">
          {league.id === "EPL" ? "Premier League" : league.nameEn}
        </h2>
        <Link
          href={`/standings?league=${league.id}`}
          className="text-sm font-semibold text-brand"
        >
          순위표 보기 →
        </Link>
      </div>
      <div className="mt-3 grid grid-cols-3 gap-[14px]">
        {matches.map((match) => (
          <MatchCard key={match.id} match={match} />
        ))}
      </div>
    </section>
  );
}
