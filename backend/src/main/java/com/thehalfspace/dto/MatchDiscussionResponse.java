package com.thehalfspace.dto;

import com.thehalfspace.entity.Match;

public record MatchDiscussionResponse(
        MatchResponse match,
        long commentCount
) {
    public static MatchDiscussionResponse of(Match match, long commentCount) {
        return new MatchDiscussionResponse(MatchResponse.from(match), commentCount);
    }
}
