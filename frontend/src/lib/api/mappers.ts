import type { LeagueId, Team } from "../types";

export interface TeamResponseDto {
  id: number;
  name: string;
  shortName: string;
  tla: string;
  crestUrl: string;
}

export function mapTeam(raw: TeamResponseDto, league: LeagueId): Team {
  return {
    id: String(raw.id),
    code: raw.tla,
    name: raw.shortName || raw.name,
    league,
  };
}
