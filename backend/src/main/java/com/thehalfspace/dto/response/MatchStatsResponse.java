package com.thehalfspace.dto.response;

public record MatchStatsResponse(
        Long matchId,
        FanDistribution fanDistribution
) {
    public record FanDistribution(
            long homeVotes,
            long drawVotes,
            long awayVotes,
            Double homeWinPct,
            Double drawPct,
            Double awayWinPct
    ) {}
}
