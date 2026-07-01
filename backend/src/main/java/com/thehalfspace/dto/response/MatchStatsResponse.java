package com.thehalfspace.dto.response;

public record MatchStatsResponse(
        AiPredictionResponse aiPrediction,
        FanDistribution fanDistribution
) {
    public record FanDistribution(
            Long home,
            Long draw,
            Long away
    ) {}
}
