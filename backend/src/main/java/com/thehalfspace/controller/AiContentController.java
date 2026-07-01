package com.thehalfspace.controller;

import com.thehalfspace.dto.ApiResponse;
import com.thehalfspace.dto.response.AiContentResponse;
import com.thehalfspace.dto.response.AiPredictionResponse;
import com.thehalfspace.dto.response.MatchStatsResponse;
import com.thehalfspace.service.AiContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/matches/{matchId}")
@RequiredArgsConstructor
public class AiContentController {

    private final AiContentService aiContentService;

    @GetMapping("/preview")
    public ApiResponse<AiContentResponse> getPreview(@PathVariable Long matchId) {
        return ApiResponse.of(aiContentService.getPreview(matchId));
    }

    @GetMapping("/review")
    public ApiResponse<AiContentResponse> getReview(@PathVariable Long matchId) {
        return ApiResponse.of(aiContentService.getReview(matchId));
    }

    @GetMapping("/prediction")
    public ApiResponse<AiPredictionResponse> getPrediction(@PathVariable Long matchId) {
        return ApiResponse.of(aiContentService.getAiPrediction(matchId));
    }

    @GetMapping("/stats")
    public ApiResponse<MatchStatsResponse> getStats(@PathVariable Long matchId) {
        return ApiResponse.of(aiContentService.getMatchStats(matchId));
    }
}
