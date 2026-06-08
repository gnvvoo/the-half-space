package com.thehalfspace.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "matches", indexes = {
        @Index(name = "idx_matches_status",      columnList = "status"),
        @Index(name = "idx_matches_utc_date",    columnList = "utc_date"),
        @Index(name = "idx_matches_competition", columnList = "competition_id, season")
})
public class Match {

    @Id
    private Long id;                        // football-data.org match id

    @Column(name = "competition_id", nullable = false, length = 10)
    private String competitionId;           // PL, PD, BL1, SA, FL1

    @Column(nullable = false, length = 9)
    private String season;                  // 2025-26

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MatchStatus status;

    @Column(name = "match_day")
    private Integer matchDay;               // 라운드

    @Column(name = "utc_date", nullable = false)
    private Instant utcDate;               // 경기 시간 (UTC)

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private MatchWinner winner;

    @Column(length = 200)
    private String venue;                   // 경기장

    @Builder.Default
    @Column(name = "fetched_at")
    private Instant fetchedAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    public void update(MatchStatus status, Integer homeScore, Integer awayScore, MatchWinner winner) {
        this.status    = status;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.winner    = winner;
        this.updatedAt = Instant.now();
        this.fetchedAt = Instant.now();
    }
}
