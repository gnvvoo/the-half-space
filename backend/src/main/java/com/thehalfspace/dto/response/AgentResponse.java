package com.thehalfspace.dto.response;

import java.util.List;
import java.util.Map;

public record AgentResponse(
        String body,
        String summary,
        Map<String, Object> reasoning,
        Integer tokenUsed,
        String modelVersion,
        List<Map<String, Object>> toolsCalled,
        PredictionResult prediction
) {
    public record PredictionResult(
            Double homeWinPct,
            Double drawPct,
            Double awayWinPct,
            String predictedScore,
            Map<String, Object> reasoning
    ) {}
}
