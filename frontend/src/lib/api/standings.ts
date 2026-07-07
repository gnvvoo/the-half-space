import type { LeagueId, StandingRow } from "../types";
import { competitionCode } from "../leagues";
import { apiFetch } from "./http";
import { mapTeam, type TeamResponseDto } from "./mappers";

interface StandingResponseDto {
  position: number;
  team: TeamResponseDto;
  played: number;
  won: number;
  draw: number;
  lost: number;
  goalsFor: number;
  goalsAgainst: number;
  goalDiff: number;
  points: number;
}

function mapStanding(raw: StandingResponseDto, league: LeagueId): StandingRow {
  return {
    position: raw.position,
    team: mapTeam(raw.team, league),
    played: raw.played,
    won: raw.won,
    drawn: raw.draw,
    lost: raw.lost,
    goalDiff: raw.goalDiff,
    points: raw.points,
  };
}

// GET /standings?competition=
export async function fetchStandings(league: LeagueId): Promise<StandingRow[]> {
  const params = new URLSearchParams({ competition: competitionCode(league) });
  const rows = await apiFetch<StandingResponseDto[]>(`/standings?${params}`, {
    next: { revalidate: 3600 },
  });
  return rows.map((row) => mapStanding(row, league));
}
