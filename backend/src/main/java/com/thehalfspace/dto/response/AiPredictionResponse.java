package com.thehalfspace.dto.response;

import com.thehalfspace.entity.AiPrediction;

import java.util.UUID;

public record AiPredictionResponse(
        UUID id,
        Long matchId,
        Double homeWinPct,
        Double drawPct,
        Double awayWinPct,
        String predictedScore,
        String actualResult,
        Boolean isCorrect,
        String createdAt
) {
    public static AiPredictionResponse from(AiPrediction aiPrediction) {
        return new AiPredictionResponse(
                aiPrediction.getId(),
                aiPrediction.getMatch() != null ? aiPrediction.getMatch().getId() : null,
                aiPrediction.getHomeWinPct(),
                aiPrediction.getDrawPct(),
                aiPrediction.getAwayWinPct(),
                aiPrediction.getPredictedScore(),
                aiPrediction.getActualResult(),
                aiPrediction.getIsCorrect(),
                aiPrediction.getCreatedAt().toString()
        );
    }
}
