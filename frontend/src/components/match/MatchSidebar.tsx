import { FormRow } from "@/components/ui/FormBadge";
import { FanPredictionWidget } from "./FanPredictionWidget";
import type { Match, StandingRow } from "@/lib/types";
import { formatSigned } from "@/lib/utils";

export function MatchSidebar({
  match,
  standings,
}: {
  match: Match;
  standings: StandingRow[];
}) {
  const homeRow = standings.find((r) => r.team.id === match.homeTeam.id);
  const awayRow = standings.find((r) => r.team.id === match.awayTeam.id);

  return (
    <aside className="flex w-[280px] shrink-0 flex-col gap-6">
      <div className="border border-line p-4">
        <p className="text-xs font-bold text-muted">최근 폼 (최근 5경기)</p>
        <div className="mt-3 flex flex-col gap-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-ink">{match.homeTeam.name}</span>
            <FormRow results={match.recentForm.home} />
          </div>
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-ink">{match.awayTeam.name}</span>
            <FormRow results={match.recentForm.away} />
          </div>
        </div>
      </div>

      <div className="border border-line p-4">
        <p className="text-xs font-bold text-muted">리그 순위</p>
        <div className="mt-3 flex flex-col gap-2 text-xs">
          {[homeRow, awayRow].map(
            (row) =>
              row && (
                <div key={row.team.id} className="flex items-center justify-between">
                  <span className="text-ink">
                    {row.position}위 {row.team.name}
                  </span>
                  <span className="text-muted">
                    {row.points}점 ({formatSigned(row.goalDiff)})
                  </span>
                </div>
              )
          )}
        </div>
      </div>

      <div className="border border-line p-4">
        <p className="text-xs font-bold text-muted">상대 전적</p>
        <p className="mt-3 text-xs leading-relaxed text-ink/80">{match.headToHead}</p>
      </div>

      <FanPredictionWidget
        initial={match.fanPrediction}
        homeLabel={match.homeTeam.name}
        awayLabel={match.awayTeam.name}
      />
    </aside>
  );
}
