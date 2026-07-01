package com.thehalfspace.dto.response;

import com.thehalfspace.entity.AiPrediction;

import java.util.Map;

public record AiPredictionResponse(
        Long matchId,
        Double homeWinPct,
        Double drawPct,
        Double awayWinPct,
        String predictedScore,
        Map<String, Object> reasoning
) {
    public static AiPredictionResponse from(AiPrediction aiPrediction) {
        return new AiPredictionResponse(
                aiPrediction.getMatch() != null ? aiPrediction.getMatch().getId() : null,
                aiPrediction.getHomeWinPct(),
                aiPrediction.getDrawPct(),
                aiPrediction.getAwayWinPct(),
                aiPrediction.getPredictedScore(),
                aiPrediction.getReasoning()
        );
    }
}
