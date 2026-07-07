import { CrestPlaceholder } from "@/components/ui/CrestPlaceholder";
import { MatchStatusIndicator } from "@/components/ui/MatchStatusIndicator";
import { leagueDisplayName } from "@/lib/leagues";
import type { Match } from "@/lib/types";

export function Scoreboard({ match }: { match: Match }) {
  return (
    <section className="flex flex-col items-center gap-4 border-b border-line px-10 py-9">
      <div className="flex items-center gap-2">
        <MatchStatusIndicator match={match} />
        <span className="text-xs text-muted">
          {leagueDisplayName(match.league)} · Matchweek {match.matchweek} · {match.venue}
        </span>
      </div>

      <div className="flex items-center gap-10">
        <div className="flex flex-col items-center gap-3">
          <CrestPlaceholder code={match.homeTeam.code} size="lg" />
          <span className="text-sm font-semibold text-ink">{match.homeTeam.name}</span>
        </div>

        <div className="text-5xl font-black text-ink">
          {match.homeScore ?? "-"} – {match.awayScore ?? "-"}
        </div>

        <div className="flex flex-col items-center gap-3">
          <CrestPlaceholder code={match.awayTeam.code} size="lg" />
          <span className="text-sm font-semibold text-ink">{match.awayTeam.name}</span>
        </div>
      </div>
    </section>
  );
}
