package com.thehalfspace.repository;

import com.thehalfspace.entity.Standing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StandingRepository extends JpaRepository<Standing, Long> {

    List<Standing> findByCompetitionIdAndSeasonOrderByPosition(
            String competitionId, String season);

    void deleteByCompetitionIdAndSeason(String competitionId, String season);
}
