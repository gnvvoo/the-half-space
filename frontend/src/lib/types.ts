// Domain types for The Half Space.
// These mirror the shape we expect back from the Spring Boot REST API
// (`localhost:8080/api/v1`) so that swapping the mock data layer in
// `src/lib/api/*` for real `fetch` calls later requires no changes here.

export type LeagueId = "EPL" | "LALIGA" | "BUNDESLIGA" | "SERIEA" | "LIGUE1";

export interface League {
  id: LeagueId;
  nameKo: string;
  nameEn: string;
  slug: string;
}

export interface Team {
  id: string;
  /** 3-letter crest abbreviation shown in the placeholder crest circle */
  code: string;
  name: string;
  league: LeagueId;
}

export type MatchStatus = "SCHEDULED" | "LIVE" | "FINISHED";

export type FormResult = "W" | "D" | "L";

export interface VoteDistribution {
  home: number;
  draw: number;
  away: number;
}

export interface FanPrediction extends VoteDistribution {
  totalVotes: number;
}

export interface Match {
  id: string;
  league: LeagueId;
  status: MatchStatus;
  /** e.g. "18:30" for scheduled matches */
  kickoffTime?: string;
  /** elapsed minute, only present while LIVE */
  minute?: number;
  matchweek: number;
  venue: string;
  homeTeam: Team;
  awayTeam: Team;
  homeScore: number | null;
  awayScore: number | null;
  aiPrediction: VoteDistribution;
  fanPrediction: FanPrediction;
  recentForm: {
    home: FormResult[];
    away: FormResult[];
  };
  headToHead: string;
  aiPreviewTitle: string;
  aiPreviewBody: string[];
  /** Pull-quote rendered with the red left border + italic treatment */
  aiPreviewQuote?: string;
  aiReviewTitle: string;
  aiReviewBody: string[];
  aiReviewQuote?: string;
}

export interface Discussion {
  id: string;
  title: string;
  likes: number;
  commentCount: number;
}

export interface Comment {
  id: string;
  author: string;
  content: string;
  likes: number;
  createdAt: string;
  replies: Comment[];
}

export interface StandingRow {
  position: number;
  team: Team;
  played: number;
  won: number;
  drawn: number;
  lost: number;
  goalDiff: number;
  points: number;
}

export type StandingZone = "ucl" | "relegation" | null;

export interface FavoriteTeamRef {
  id: string;
  code: string;
  name: string;
}

export interface PredictionHistoryItem {
  id: string;
  homeTeam: string;
  awayTeam: string;
  homeScore: number;
  awayScore: number;
  prediction: "home" | "draw" | "away";
  correct: boolean;
}

export interface User {
  id: string;
  nickname: string;
  email: string;
  favoriteTeams: FavoriteTeamRef[];
  predictionAccuracy: number;
  totalPredictions: number;
  correctPredictions: number;
  recentPredictions: PredictionHistoryItem[];
}
