package com.thehalfspace.dto.response;

import java.util.List;
import java.util.Map;

public record AgentResponse(
        String type,
        Long matchId,
        String body,
        String summary,
        Map<String, Object> reasoning,
        PredictionResult prediction,
        List<Map<String, Object>> toolsCalled,
        Integer totalTokens,
        String modelVersion
) {
    public record PredictionResult(
            Double homeWinPct,
            Double drawPct,
            Double awayWinPct,
            String predictedScore
    ) {}
}
