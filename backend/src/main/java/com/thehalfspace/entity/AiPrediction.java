package com.thehalfspace.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ai_predictions")
public class AiPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", unique = true)
    private Match match;

    @Column(name = "home_win_pct", nullable = false)
    private Double homeWinPct;

    @Column(name = "draw_pct", nullable = false)
    private Double drawPct;

    @Column(name = "away_win_pct", nullable = false)
    private Double awayWinPct;

    @Column(name = "predicted_score", length = 10)
    private String predictedScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> reasoning;

    @Column(name = "actual_result", length = 10)
    private String actualResult;

    // NULL(경기 전) → true/false(경기 후 배치 업데이트)
    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    public static AiPrediction of(Match match, Double homeWinPct, Double drawPct, Double awayWinPct,
                                   String predictedScore, Map<String, Object> reasoning) {
        return AiPrediction.builder()
                .match(match)
                .homeWinPct(homeWinPct)
                .drawPct(drawPct)
                .awayWinPct(awayWinPct)
                .predictedScore(predictedScore)
                .reasoning(reasoning)
                .build();
    }
}
