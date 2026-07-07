import Link from "next/link";
import { CrestPlaceholder } from "@/components/ui/CrestPlaceholder";
import { MatchStatusIndicator } from "@/components/ui/MatchStatusIndicator";
import type { Match } from "@/lib/types";

function TeamRow({
  code,
  name,
  score,
}: {
  code: string;
  name: string;
  score: number | null;
}) {
  return (
    <div className="flex items-center justify-between">
      <div className="flex items-center gap-2">
        <CrestPlaceholder code={code} size="sm" />
        <span className="text-sm font-medium text-ink">{name}</span>
      </div>
      {score != null && <span className="text-sm font-bold text-ink">{score}</span>}
    </div>
  );
}

export function MatchCard({ match }: { match: Match }) {
  return (
    <Link
      href={`/match/${match.id}`}
      className="flex flex-col gap-[10px] border border-line p-4 hover:border-ink"
    >
      <MatchStatusIndicator match={match} />
      <TeamRow
        code={match.homeTeam.code}
        name={match.homeTeam.name}
        score={match.homeScore}
      />
      <TeamRow
        code={match.awayTeam.code}
        name={match.awayTeam.name}
        score={match.awayScore}
      />
    </Link>
  );
}
