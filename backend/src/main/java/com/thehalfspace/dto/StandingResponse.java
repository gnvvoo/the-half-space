package com.thehalfspace.dto;

import com.thehalfspace.entity.Standing;

public record StandingResponse(
        Integer position,
        TeamSummary team,
        Integer played,
        Integer won,
        Integer draw,
        Integer lost,
        Integer goalsFor,
        Integer goalsAgainst,
        Integer goalDiff,
        Integer points
) {
    public static StandingResponse from(Standing standing) {
        return new StandingResponse(
                standing.getPosition(),
                TeamSummary.from(standing.getTeam()),
                standing.getPlayed(),
                standing.getWon(),
                standing.getDraw(),
                standing.getLost(),
                standing.getGoalsFor(),
                standing.getGoalsAgainst(),
                standing.getGoalDiff(),
                standing.getPoints()
        );
    }
}
