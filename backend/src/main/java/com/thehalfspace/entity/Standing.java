package com.thehalfspace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "standings", indexes = {
        @Index(name = "idx_standings_comp_season",
                columnList = "competition_id, season, team_id",
                unique = false)
})
public class Standing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "competition_id", nullable = false, length = 10)
    private String competitionId;           // PL, PD, BL1, SA, FL1

    @Column(nullable = false, length = 9)
    private String season;                  // 2025-26

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;                      // 팀

    @Column(nullable = false)
    private Integer position;              // 순위

    @Column(nullable = false)
    private Integer played = 0;            // 경기 수

    @Column(nullable = false)
    private Integer won = 0;               // 승

    @Column(nullable = false)
    private Integer draw = 0;              // 무

    @Column(nullable = false)
    private Integer lost = 0;              // 패

    @Column(name = "goals_for", nullable = false)
    private Integer goalsFor = 0;          // 득점

    @Column(name = "goals_against", nullable = false)
    private Integer goalsAgainst = 0;      // 실점

    @Column(name = "goal_diff", nullable = false)
    private Integer goalDiff = 0;          // 득실차

    @Column(nullable = false)
    private Integer points = 0;            // 승점

    @Column(name = "fetched_at")
    private Instant fetchedAt = Instant.now();

    // CLI 응답으로 Standing 생성
    public static Standing of(String competitionId, String season, Team team,
                              int position, int played, int won, int draw,
                              int lost, int goalsFor, int goalsAgainst,
                              int goalDiff, int points) {
        Standing s = new Standing();
        s.competitionId = competitionId;
        s.season        = season;
        s.team          = team;
        s.position      = position;
        s.played        = played;
        s.won           = won;
        s.draw          = draw;
        s.lost          = lost;
        s.goalsFor      = goalsFor;
        s.goalsAgainst  = goalsAgainst;
        s.goalDiff      = goalDiff;
        s.points        = points;
        return s;
    }
}