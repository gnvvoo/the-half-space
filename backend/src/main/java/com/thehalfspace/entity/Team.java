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

    // API 응답으로 Team 생성
    public static Team of(Long id, String name, String shortName,
                          String tla, String crestUrl, String competitionId) {
        Team team = new Team();
        team.id            = id;
        team.name          = name;
        team.shortName     = shortName;
        team.tla           = tla;
        team.crestUrl      = crestUrl;
        team.competitionId = competitionId;
        return team;
    }

    // 팀 정보 업데이트 (API 재조회 시)
    public void update(String name, String shortName, String tla, String crestUrl) {
        this.name      = name;
        this.shortName = shortName;
        this.tla       = tla;
        this.crestUrl  = crestUrl;
    }
}