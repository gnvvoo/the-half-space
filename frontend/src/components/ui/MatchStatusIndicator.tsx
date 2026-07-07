import { LiveBadge } from "./LiveBadge";
import type { Match } from "@/lib/types";

export function MatchStatusIndicator({ match }: { match: Match }) {
  if (match.status === "LIVE") return <LiveBadge minute={match.minute} />;
  if (match.status === "FINISHED")
    return <span className="text-xs font-semibold text-success">종료</span>;
  return <span className="text-xs font-medium text-muted">{match.kickoffTime}</span>;
}
