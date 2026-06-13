package com.thehalfspace.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "agent_executions", indexes = {
        @Index(name = "idx_agent_exec_match",  columnList = "match_id"),
        @Index(name = "idx_agent_exec_status", columnList = "status, created_at")
})
public class AgentExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false, length = 30)
    private TriggerType triggerType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_content_id")
    private AiContent aiContent;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tools_called", columnDefinition = "jsonb")
    private List<Map<String, Object>> toolsCalled;

    @Column(name = "total_tokens")
    private Integer totalTokens;

    @Column(name = "latency_ms")
    private Integer latencyMs;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExecutionStatus status;

    @Column(name = "error_msg", columnDefinition = "TEXT")
    private String errorMsg;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    public static AgentExecution of(Match match, TriggerType triggerType) {
        return AgentExecution.builder()
                .match(match)
                .triggerType(triggerType)
                .status(ExecutionStatus.IN_PROGRESS)
                .build();
    }

    public void complete(Integer totalTokens, Integer latencyMs) {
        this.status = ExecutionStatus.SUCCESS;
        this.totalTokens = totalTokens;
        this.latencyMs = latencyMs;
    }

    public void fail(String errorMsg) {
        this.status = ExecutionStatus.FAILED;
        this.errorMsg = errorMsg;
    }
}
