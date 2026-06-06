package com.thehalfspace.service;

import com.thehalfspace.dto.StandingResponse;
import com.thehalfspace.repository.StandingRepository;
import com.thehalfspace.util.SeasonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StandingService {

    private final StandingRepository standingRepository;

    @Cacheable(value = "standings", key = "#competitionId")
    public List<StandingResponse> getStandings(String competitionId) {
        return standingRepository
                .findByCompetitionIdAndSeasonOrderByPosition(competitionId, SeasonUtils.currentSeason())
                .stream()
                .map(StandingResponse::from)
                .toList();
    }
}
