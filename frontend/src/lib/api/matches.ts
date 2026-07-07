import type { LeagueId, Match, MatchStatus } from "../types";
import { LEAGUES, competitionCode, leagueFromCompetition } from "../leagues";
import { apiFetch, ApiRequestError } from "./http";
import { mapTeam, type TeamResponseDto } from "./mappers";

interface MatchResponseDto {
  id: number;
  competitionId: string;
  season: string;
  matchDay: number;
  status: string;
  utcDate: string;
  homeTeam: TeamResponseDto;
  awayTeam: TeamResponseDto;
  homeScore: number | null;
  awayScore: number | null;
  winner: string | null;
  venue: string | null;
}

const LIVE_STATUSES = new Set(["LIVE", "IN_PLAY", "PAUSED"]);
const FINISHED_STATUSES = new Set(["FINISHED", "AWARDED"]);

function mapStatus(raw: string): MatchStatus {
  if (LIVE_STATUSES.has(raw)) return "LIVE";
  if (FINISHED_STATUSES.has(raw)) return "FINISHED";
  return "SCHEDULED";
}

function formatKickoffTime(utcDate: string): string {
  return new Date(utcDate).toLocaleTimeString("ko-KR", {
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
    timeZone: "Asia/Seoul",
  });
}

type CoreMatchFields = Omit<
  Match,
  | "aiPrediction"
  | "fanPrediction"
  | "recentForm"
  | "headToHead"
  | "aiPreviewTitle"
  | "aiPreviewBody"
  | "aiPreviewQuote"
  | "aiReviewTitle"
  | "aiReviewBody"
  | "aiReviewQuote"
>;

/**
 * AI Preview/Review, AI 승부 예측, 팬 투표 분포는 별도 엔드포인트
 * (`/matches/{id}/preview|review|prediction|stats`)에서 제공되며 아직 프론트에
 * 연결하지 않았다. 연결 전까지는 플레이스홀더로 채워 기존 컴포넌트가 깨지지 않게 한다.
 */
function withContentPlaceholders(match: CoreMatchFields): Match {
  return {
    ...match,
    aiPrediction: { home: 0, draw: 0, away: 0 },
    fanPrediction: { home: 0, draw: 0, away: 0, totalVotes: 0 },
    recentForm: { home: [], away: [] },
    headToHead: "상대 전적 데이터를 준비 중입니다.",
    aiPreviewTitle: "AI 프리뷰 준비 중",
    aiPreviewBody: ["AI 프리뷰가 곧 제공될 예정입니다."],
    aiReviewTitle: "AI 리뷰 준비 중",
    aiReviewBody: ["AI 리뷰가 곧 제공될 예정입니다."],
  };
}

function mapMatch(raw: MatchResponseDto): Match {
  const league = leagueFromCompetition(raw.competitionId);
  const status = mapStatus(raw.status);
  return withContentPlaceholders({
    id: String(raw.id),
    league,
    status,
    kickoffTime: status === "SCHEDULED" ? formatKickoffTime(raw.utcDate) : undefined,
    matchweek: raw.matchDay,
    venue: raw.venue ?? "",
    homeTeam: mapTeam(raw.homeTeam, league),
    awayTeam: mapTeam(raw.awayTeam, league),
    homeScore: raw.homeScore,
    awayScore: raw.awayScore,
  });
}

interface DateRange {
  from: string;
  to: string;
}

function defaultDateRange(): DateRange {
  const fmt = (d: Date) => d.toISOString().slice(0, 10);
  const from = new Date();
  from.setDate(from.getDate() - 7);
  const to = new Date();
  to.setDate(to.getDate() + 21);
  return { from: fmt(from), to: fmt(to) };
}

// GET /matches?competition=&from=&to=
export async function fetchMatchesByLeague(
  league: LeagueId,
  range: DateRange = defaultDateRange()
): Promise<Match[]> {
  const params = new URLSearchParams({
    competition: competitionCode(league),
    from: range.from,
    to: range.to,
  });
  const rows = await apiFetch<MatchResponseDto[]>(`/matches?${params}`, {
    next: { revalidate: 300 },
  });
  return rows.map(mapMatch);
}

export async function fetchAllMatches(): Promise<Match[]> {
  const range = defaultDateRange();
  const perLeague = await Promise.all(
    LEAGUES.map((league) => fetchMatchesByLeague(league.id, range))
  );
  return perLeague.flat();
}

// GET /matches/{id}
export async function fetchMatchById(id: string): Promise<Match | undefined> {
  try {
    const raw = await apiFetch<MatchResponseDto>(`/matches/${id}`, {
      next: { revalidate: 300 },
    });
    return mapMatch(raw);
  } catch (err) {
    if (err instanceof ApiRequestError && err.status === 404) return undefined;
    throw err;
  }
}
