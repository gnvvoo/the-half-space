import { CrestPlaceholder } from "@/components/ui/CrestPlaceholder";
import { getStandingZone } from "@/lib/data/standings";
import type { StandingRow } from "@/lib/types";
import { cx, formatSigned } from "@/lib/utils";

const GRID_COLS = "grid-cols-[44px_1fr_60px_44px_44px_44px_70px_60px]";

export function StandingsTable({ rows }: { rows: StandingRow[] }) {
  return (
    <div className="mt-6">
      <div
        className={cx(
          GRID_COLS,
          "grid border-b border-line px-4 py-3 text-xs font-bold text-muted"
        )}
      >
        <span>#</span>
        <span>팀</span>
        <span className="text-right">경기</span>
        <span className="text-right">승</span>
        <span className="text-right">무</span>
        <span className="text-right">패</span>
        <span className="text-right">득실차</span>
        <span className="text-right">승점</span>
      </div>

      {rows.map((row) => {
        const zone = getStandingZone(row, rows.length);
        return (
          <div
            key={row.team.id}
            className={cx(
              GRID_COLS,
              "grid items-center border-b border-line py-[14px] pl-4 pr-4 text-sm",
              zone === "ucl" && "border-l-[3px] border-l-success",
              zone === "relegation" && "border-l-[3px] border-l-brand"
            )}
          >
            <span className="font-bold text-ink">{row.position}</span>
            <span className="flex items-center gap-2">
              <CrestPlaceholder code={row.team.code} size="sm" />
              <span className="font-medium text-ink">{row.team.name}</span>
            </span>
            <span className="text-right text-ink/80">{row.played}</span>
            <span className="text-right text-ink/80">{row.won}</span>
            <span className="text-right text-ink/80">{row.drawn}</span>
            <span className="text-right text-ink/80">{row.lost}</span>
            <span className="text-right text-ink/80">{formatSigned(row.goalDiff)}</span>
            <span className="text-right font-bold text-ink">{row.points}</span>
          </div>
        );
      })}

      <div className="mt-4 flex gap-6 text-xs text-muted">
        <span className="flex items-center gap-2">
          <span className="h-[10px] w-[10px] bg-success" /> 챔피언스리그 진출권
        </span>
        <span className="flex items-center gap-2">
          <span className="h-[10px] w-[10px] bg-brand" /> 강등권
        </span>
      </div>
    </div>
  );
}
