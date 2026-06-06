package com.thehalfspace.service;

import com.thehalfspace.dto.MatchResponse;
import com.thehalfspace.exception.NotFoundException;
import com.thehalfspace.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {

    private final MatchRepository matchRepository;

    @Cacheable(value = "matches", key = "#competitionId + ':' + #from + ':' + #to")
    public List<MatchResponse> getMatches(String competitionId, LocalDate from, LocalDate to) {
        var fromInstant = from.atStartOfDay(ZoneOffset.UTC).toInstant();
        var toInstant   = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        return matchRepository
                .findByCompetitionIdAndUtcDateBetweenOrderByUtcDate(competitionId, fromInstant, toInstant)
                .stream()
                .map(MatchResponse::from)
                .toList();
    }

    public MatchResponse getMatch(Long id) {
        return matchRepository.findById(id)
                .map(MatchResponse::from)
                .orElseThrow(() -> new NotFoundException("경기를 찾을 수 없습니다: " + id));
    }
}
