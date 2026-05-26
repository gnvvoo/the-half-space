package com.thehalfspace.repository;

import com.thehalfspace.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByCompetitionIdAndUtcDateBetweenOrderByUtcDate(
            String competitionId, Instant from, Instant to);

    List<Match> findByCompetitionIdAndSeasonOrderByMatchDayAscUtcDateAsc(
            String competitionId, String season);

    List<Match> findByStatus(String status);

    List<Match> findByCompetitionIdAndStatus(String competitionId, String status);

    long countByCompetitionId(String competitionId);
}
