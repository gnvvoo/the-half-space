package com.thehalfspace.dto;

import com.thehalfspace.entity.Match;

public record MatchResponse(
        Long id,
        String competitionId,
        String season,
        Integer matchDay,
        String status,
        String utcDate,
        TeamSummary homeTeam,
        TeamSummary awayTeam,
        Integer homeScore,
        Integer awayScore,
        String winner,
        String venue
) {
    public static MatchResponse from(Match match) {
        return new MatchResponse(
                match.getId(),
                match.getCompetitionId(),
                match.getSeason(),
                match.getMatchDay(),
                match.getStatus(),
                match.getUtcDate().toString(),
                TeamSummary.from(match.getHomeTeam()),
                TeamSummary.from(match.getAwayTeam()),
                match.getHomeScore(),
                match.getAwayScore(),
                match.getWinner(),
                match.getVenue()
        );
    }
}
