package com.thehalfspace.dto.request;

public record AgentRequest(
        String type,    // preview | review
        Long matchId
) {}
