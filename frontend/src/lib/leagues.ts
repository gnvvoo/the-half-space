import type { League, LeagueId } from "./types";

export const LEAGUES: League[] = [
  { id: "EPL", nameKo: "EPL", nameEn: "Premier League", slug: "epl" },
  { id: "LALIGA", nameKo: "La Liga", nameEn: "La Liga", slug: "laliga" },
  {
    id: "BUNDESLIGA",
    nameKo: "Bundesliga",
    nameEn: "Bundesliga",
    slug: "bundesliga",
  },
  { id: "SERIEA", nameKo: "Serie A", nameEn: "Serie A", slug: "seriea" },
  { id: "LIGUE1", nameKo: "Ligue 1", nameEn: "Ligue 1", slug: "ligue1" },
];

export const DEFAULT_LEAGUE: LeagueId = "EPL";

export function getLeague(id: LeagueId): League {
  return LEAGUES.find((l) => l.id === id) ?? LEAGUES[0];
}

export function isLeagueId(value: string | undefined | null): value is LeagueId {
  return !!value && LEAGUES.some((l) => l.id === value);
}

export function leagueDisplayName(id: LeagueId): string {
  const league = getLeague(id);
  return id === "EPL" ? "Premier League" : league.nameEn;
}

/** football-data.org 리그 코드 (backend/docs/API.md 기준). */
const COMPETITION_BY_LEAGUE: Record<LeagueId, string> = {
  EPL: "PL",
  LALIGA: "PD",
  BUNDESLIGA: "BL1",
  SERIEA: "SA",
  LIGUE1: "FL1",
};

const LEAGUE_BY_COMPETITION: Record<string, LeagueId> = Object.fromEntries(
  Object.entries(COMPETITION_BY_LEAGUE).map(([league, code]) => [code, league])
) as Record<string, LeagueId>;

export function competitionCode(league: LeagueId): string {
  return COMPETITION_BY_LEAGUE[league];
}

export function leagueFromCompetition(code: string): LeagueId {
  return LEAGUE_BY_COMPETITION[code] ?? DEFAULT_LEAGUE;
}
