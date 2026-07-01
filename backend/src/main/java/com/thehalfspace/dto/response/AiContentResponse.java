package com.thehalfspace.dto.response;

import com.thehalfspace.entity.AiContent;

import java.time.Instant;
import java.util.UUID;

public record AiContentResponse(
        UUID id,
        Long matchId,
        String type,
        String body,
        String summary,
        boolean isPublished,
        Instant createdAt
) {
    public static AiContentResponse from(AiContent aiContent) {
        return new AiContentResponse(
                aiContent.getId(),
                aiContent.getMatch() != null ? aiContent.getMatch().getId() : null,
                aiContent.getType().name(),
                aiContent.getBody(),
                aiContent.getSummary(),
                aiContent.isPublished(),
                aiContent.getCreatedAt()
        );
    }
}
