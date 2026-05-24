
package com.thehalfspace.entity;

import java.util.Arrays;

public enum Competition {

    EPL        ("PL",  "Premier League", "EPL"),
    LA_LIGA    ("PD",  "La Liga",        "LaLiga"),
    BUNDESLIGA ("BL1", "Bundesliga",     "Bundesliga"),
    SERIE_A    ("SA",  "Serie A",        "SerieA"),
    LIGUE_1    ("FL1", "Ligue 1",        "Ligue1");

    private final String competitionId; // DB 저장값 (PL, PD ...)
    private final String fullName;      // CLI 응답값 (Premier League ...)
    private final String cliCode;       // CLI 호출값 (EPL, LaLiga ...)

    Competition(String competitionId, String fullName, String cliCode) {
        this.competitionId = competitionId;
        this.fullName      = fullName;
        this.cliCode       = cliCode;
    }

    public String getCompetitionId() { return competitionId; }
    public String getFullName()      { return fullName; }
    public String getCliCode()       { return cliCode; }

    // CLI 풀네임으로 찾기 (CLI 응답 파싱 시)
    public static Competition fromFullName(String fullName) {
        return Arrays.stream(values())
                .filter(c -> c.fullName.equals(fullName))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown league: " + fullName));
    }

    // DB competitionId로 찾기
    public static Competition fromCompetitionId(String competitionId) {
        return Arrays.stream(values())
                .filter(c -> c.competitionId.equals(competitionId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown competitionId: " + competitionId));
    }

    // CLI 약어로 찾기 (CLI 호출 시)
    public static Competition fromCliCode(String cliCode) {
        return Arrays.stream(values())
                .filter(c -> c.cliCode.equals(cliCode))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown CLI code: " + cliCode));
    }
}