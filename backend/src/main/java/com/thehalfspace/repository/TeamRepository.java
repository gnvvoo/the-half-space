package com.thehalfspace.repository;

import com.thehalfspace.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByCompetitionId(String competitionId);

    Optional<Team> findByName(String name);
}
