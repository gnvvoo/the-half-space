package com.thehalfspace.dto.response;

import com.thehalfspace.entity.AiContent;
import com.thehalfspace.entity.ContentType;

import java.util.UUID;

public record AiContentResponse(
        UUID id,
        Long matchId,
        ContentType type,
        String body,
        String summary,
        Integer version,
        boolean isPublished,
        Double qualityScore,
        String createdAt
) {
    public static AiContentResponse from(AiContent aiContent) {
        return new AiContentResponse(
                aiContent.getId(),
                aiContent.getMatch() != null ? aiContent.getMatch().getId() : null,
                aiContent.getType(),
                aiContent.getBody(),
                aiContent.getSummary(),
                aiContent.getVersion(),
                aiContent.isPublished(),
                aiContent.getQualityScore(),
                aiContent.getCreatedAt().toString()
        );
    }
}
