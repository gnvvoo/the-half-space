package com.thehalfspace.repository;

import com.thehalfspace.entity.AgentExecution;
import com.thehalfspace.entity.ExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AgentExecutionRepository extends JpaRepository<AgentExecution, UUID> {

    List<AgentExecution> findByMatchId(Long matchId);

    boolean existsByMatchIdAndStatus(Long matchId, ExecutionStatus status);
}
