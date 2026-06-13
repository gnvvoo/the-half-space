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
@Table(name = "ai_contents", indexes = {
        @Index(name = "idx_ai_contents_match",     columnList = "match_id, type"),
        @Index(name = "idx_ai_contents_published", columnList = "is_published, type")
})
public class AiContent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContentType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> reasoning;

    @Column(name = "token_used")
    private Integer tokenUsed;

    @Column(name = "model_version", length = 50)
    private String modelVersion;

    @Builder.Default
    @Column(nullable = false)
    private Integer version = 1;

    @Builder.Default
    @Column(name = "is_published", nullable = false)
    private boolean isPublished = false;

    @Column(name = "quality_score")
    private Double qualityScore;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    public static AiContent of(Match match, ContentType type, String body, String summary,
                                Map<String, Object> reasoning, Integer tokenUsed, String modelVersion) {
        return AiContent.builder()
                .match(match)
                .type(type)
                .body(body)
                .summary(summary)
                .reasoning(reasoning)
                .tokenUsed(tokenUsed)
                .modelVersion(modelVersion)
                .build();
    }

    public void publish() {
        this.isPublished = true;
    }
}
