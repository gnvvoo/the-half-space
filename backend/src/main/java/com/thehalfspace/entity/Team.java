package com.thehalfspace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "teams")
public class Team {

    @Id
    private Long id;                        // football-data.org team id

    @Column(nullable = false, length = 100)
    private String name;                    // 팀 이름

    @Column(name = "short_name", length = 50)
    private String shortName;              // 팀 약식 이름

    @Column(length = 5)
    private String tla;                    // 팀 약어 (ARS, CHE ...)

    @Column(name = "crest_url")
    private String crestUrl;               // 팀 엠블럼 URL

    @Column(name = "competition_id", length = 10)
    private String competitionId;          // 리그 ID (PL, PD ...)

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    // 이름으로 임시 Team 생성 (CLI 파싱용)
    public static Team ofName(String name) {
        Team team = new Team();
        team.name = name;
        return team;
    }

    // CLI 경기 파싱 시 팀명으로 생성 (id는 이름 해시값으로 대체)
    public static Team of(Long id, String name, String competitionId) {
        Team team = new Team();
        team.id            = id;
        team.name          = name;
        team.competitionId = competitionId;
        return team;
    }
}