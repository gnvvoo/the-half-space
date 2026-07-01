package com.thehalfspace.dto.request;

import com.thehalfspace.entity.ContentType;

public record AgentRequest(
        ContentType type,
        Long matchId
) {}
