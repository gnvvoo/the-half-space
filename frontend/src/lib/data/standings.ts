import type { LeagueId, StandingRow } from "../types";
import { getTeam } from "./teams";

type Row = [teamKey: string, played: number, won: number, drawn: number, lost: number, goalDiff: number];

function build(rows: Row[]): StandingRow[] {
  return rows.map(([teamKey, played, won, drawn, lost, goalDiff], index) => ({
    position: index + 1,
    team: getTeam(teamKey),
    played,
    won,
    drawn,
    lost,
    goalDiff,
    points: won * 3 + drawn,
  }));
}

const EPL: Row[] = [
  ["mci", 34, 25, 5, 4, 42],
  ["ars", 34, 24, 4, 6, 38],
  ["liv", 34, 22, 7, 5, 31],
  ["che", 34, 19, 8, 7, 18],
  ["tot", 34, 17, 9, 8, 12],
  ["mun", 34, 15, 9, 10, 4],
  ["new", 34, 15, 8, 11, 2],
  ["avl", 34, 14, 9, 11, -1],
  ["bha", 34, 13, 10, 11, -2],
  ["whu", 34, 13, 9, 12, -4],
  ["cry", 34, 12, 10, 12, -3],
  ["wol", 34, 12, 8, 14, -8],
  ["ful", 34, 11, 10, 13, -9],
  ["eve", 34, 10, 11, 13, -10],
  ["bre", 34, 10, 9, 15, -12],
  ["nfo", 34, 9, 10, 15, -14],
  ["bou", 34, 9, 8, 17, -18],
  ["lei", 34, 7, 9, 18, -22],
  ["ips", 34, 6, 8, 20, -28],
  ["sou", 34, 4, 7, 23, -35],
];

const LALIGA: Row[] = [
  ["rma", 30, 22, 5, 3, 40],
  ["bar", 30, 21, 6, 3, 45],
  ["atm", 30, 19, 7, 4, 30],
  ["ath", 30, 17, 8, 5, 20],
  ["vil", 30, 16, 7, 7, 15],
  ["rso", 30, 14, 9, 7, 10],
  ["bet", 30, 13, 9, 8, 5],
  ["gir", 30, 12, 9, 9, 2],
  ["cel", 30, 12, 8, 10, 0],
  ["sev", 30, 11, 9, 10, -3],
  ["osa", 30, 10, 10, 10, -4],
  ["ray", 30, 10, 9, 11, -5],
  ["mal", 30, 9, 10, 11, -7],
  ["val", 30, 9, 9, 12, -9],
  ["get", 30, 8, 10, 12, -11],
  ["ala", 30, 8, 9, 13, -13],
  ["esp", 30, 7, 9, 14, -16],
  ["lpa", 30, 6, 8, 16, -20],
  ["leg", 30, 5, 8, 17, -24],
  ["vld", 30, 3, 6, 21, -40],
];

const BUNDESLIGA: Row[] = [
  ["bay", 28, 22, 3, 3, 50],
  ["bvb", 28, 17, 6, 5, 22],
  ["rbl", 28, 16, 7, 5, 20],
  ["b04", 28, 15, 8, 5, 18],
  ["sge", 28, 14, 7, 7, 12],
  ["vfb", 28, 13, 8, 7, 8],
  ["bmg", 28, 11, 9, 8, 2],
  ["svw", 28, 10, 9, 9, -1],
  ["scf", 28, 10, 8, 10, -3],
  ["wob", 28, 9, 9, 10, -5],
  ["m05", 28, 9, 8, 11, -6],
  ["tsg", 28, 8, 9, 11, -8],
  ["fcu", 28, 8, 8, 12, -10],
  ["fca", 28, 7, 9, 12, -12],
  ["koe", 28, 7, 7, 14, -16],
  ["boc", 28, 6, 7, 15, -20],
  ["kie", 28, 5, 7, 16, -25],
  ["stp", 28, 4, 6, 18, -32],
];

const SERIEA: Row[] = [
  ["int", 32, 22, 6, 4, 38],
  ["nap", 32, 21, 7, 4, 35],
  ["juv", 32, 18, 9, 5, 22],
  ["ata", 32, 17, 9, 6, 25],
  ["mil", 32, 16, 8, 8, 15],
  ["rom", 32, 14, 10, 8, 10],
  ["laz", 32, 13, 10, 9, 6],
  ["fio", 32, 12, 11, 9, 4],
  ["bol", 32, 11, 11, 10, 1],
  ["tor", 32, 10, 11, 11, -2],
  ["udi", 32, 10, 9, 13, -5],
  ["gen", 32, 9, 10, 13, -8],
  ["ver", 32, 8, 11, 13, -10],
  ["com", 32, 8, 10, 14, -12],
  ["cag", 32, 8, 9, 15, -15],
  ["lec", 32, 7, 10, 15, -17],
  ["par", 32, 7, 9, 16, -19],
  ["emp", 32, 6, 9, 17, -22],
  ["ven", 32, 5, 8, 19, -30],
  ["mon2", 32, 4, 7, 21, -38],
];

const LIGUE1: Row[] = [
  ["psg", 28, 22, 4, 2, 50],
  ["mar", 28, 18, 6, 4, 25],
  ["mon", 28, 16, 7, 5, 18],
  ["lyo", 28, 15, 8, 5, 15],
  ["lil", 28, 13, 9, 6, 10],
  ["nic", 28, 12, 9, 7, 7],
  ["lens", 28, 11, 9, 8, 4],
  ["ren", 28, 10, 10, 8, 2],
  ["str", 28, 10, 9, 9, 0],
  ["bre2", 28, 9, 9, 10, -3],
  ["tou", 28, 9, 8, 11, -5],
  ["nan", 28, 8, 9, 11, -6],
  ["rei", 28, 8, 8, 12, -8],
  ["mtp", 28, 7, 9, 12, -10],
  ["ang", 28, 6, 9, 13, -13],
  ["aux", 28, 6, 8, 14, -16],
  ["hav", 28, 5, 7, 16, -22],
  ["sai", 28, 3, 7, 18, -30],
];

const STANDINGS_BY_LEAGUE: Record<LeagueId, StandingRow[]> = {
  EPL: build(EPL),
  LALIGA: build(LALIGA),
  BUNDESLIGA: build(BUNDESLIGA),
  SERIEA: build(SERIEA),
  LIGUE1: build(LIGUE1),
};

export function getStandingsForLeague(league: LeagueId): StandingRow[] {
  return STANDINGS_BY_LEAGUE[league];
}

/** 1~4위 챔스권, 하위 3팀 강등권 — 리그 팀 수와 무관하게 하위 3팀 기준. */
export function getStandingZone(row: StandingRow, totalTeams: number): "ucl" | "relegation" | null {
  if (row.position <= 4) return "ucl";
  if (row.position > totalTeams - 3) return "relegation";
  return null;
}
