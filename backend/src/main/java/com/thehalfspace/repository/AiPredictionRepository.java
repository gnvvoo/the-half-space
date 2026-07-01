package com.thehalfspace.repository;

import com.thehalfspace.entity.AiPrediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AiPredictionRepository extends JpaRepository<AiPrediction, UUID> {

    Optional<AiPrediction> findByMatchId(Long matchId);

    boolean existsByMatchId(Long matchId);
}
