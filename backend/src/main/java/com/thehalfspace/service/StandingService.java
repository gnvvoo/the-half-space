package com.thehalfspace.service;

import com.thehalfspace.dto.StandingResponse;
import com.thehalfspace.repository.StandingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StandingService {

    private final StandingRepository standingRepository;

    public List<StandingResponse> getStandings(String competitionId) {
        return standingRepository
                .findByCompetitionIdAndSeasonOrderByPosition(competitionId, currentSeason())
                .stream()
                .map(StandingResponse::from)
                .toList();
    }

    private String currentSeason() {
        LocalDate now = LocalDate.now(ZoneOffset.UTC);
        int startYear = now.getMonthValue() >= 8 ? now.getYear() : now.getYear() - 1;
        return startYear + "-" + String.format("%02d", (startYear + 1) % 100);
    }
}
