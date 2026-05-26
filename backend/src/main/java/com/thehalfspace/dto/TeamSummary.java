package com.thehalfspace.dto;

import com.thehalfspace.entity.Team;

public record TeamSummary(
        Long id,
        String name,
        String shortName,
        String tla,
        String crestUrl
) {
    public static TeamSummary from(Team team) {
        return new TeamSummary(
                team.getId(),
                team.getName(),
                team.getShortName(),
                team.getTla(),
                team.getCrestUrl()
        );
    }
}
