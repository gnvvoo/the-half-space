import type { User } from "../types";

export const MOCK_USER: User = {
  id: "u1",
  nickname: "gooner_92",
  email: "gooner92@example.com",
  favoriteTeams: [
    { id: "ars", code: "ARS", name: "Arsenal" },
    { id: "rma", code: "RMA", name: "Real Madrid" },
  ],
  predictionAccuracy: 68,
  totalPredictions: 47,
  correctPredictions: 32,
  recentPredictions: [
    {
      id: "p1",
      homeTeam: "Arsenal",
      awayTeam: "Man City",
      homeScore: 2,
      awayScore: 1,
      prediction: "home",
      correct: true,
    },
    {
      id: "p2",
      homeTeam: "Real Madrid",
      awayTeam: "Barcelona",
      homeScore: 0,
      awayScore: 0,
      prediction: "away",
      correct: false,
    },
  ],
};
