package com.thehalfspace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
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
    private Team homeTeam;                  // 홈팀

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;                  // 원정팀

    @Column(nullable = false, length = 20)
    private String status;                  // SCHEDULED│TIMED│LIVE│FINISHED│POSTPONED

    @Column(name = "match_day")
    private Integer matchDay;               // 라운드

    @Column(name = "utc_date", nullable = false)
    private Instant utcDate;                // 경기 시간 (UTC)

    @Column(name = "home_score")
    private Integer homeScore;              // 홈팀 점수

    @Column(name = "away_score")
    private Integer awayScore;              // 원정팀 점수

    @Column(length = 10)
    private String winner;                  // HOME_TEAM│AWAY_TEAM│DRAW

    @Column(length = 200)
    private String venue;                   // 경기장

    @Column(name = "fetched_at")
    private Instant fetchedAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    // 경기 상태 업데이트
    public void update(String status, Integer homeScore,
                       Integer awayScore, String winner) {
        this.status    = status;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.winner    = winner;
        this.updatedAt = Instant.now();
        this.fetchedAt = Instant.now();
    }

    // API 응답으로 Match 생성
    public static Match of(Long id, String competitionId, String season,
                           Team homeTeam, Team awayTeam, String status,
                           Integer matchDay, Instant utcDate,
                           Integer homeScore, Integer awayScore,
                           String winner, String venue) {
        Match match = new Match();
        match.id            = id;
        match.competitionId = competitionId;
        match.season        = season;
        match.homeTeam      = homeTeam;
        match.awayTeam      = awayTeam;
        match.status        = status;
        match.matchDay      = matchDay;
        match.utcDate       = utcDate;
        match.homeScore     = homeScore;
        match.awayScore     = awayScore;
        match.winner        = winner;
        match.venue         = venue;
        return match;
    }
}