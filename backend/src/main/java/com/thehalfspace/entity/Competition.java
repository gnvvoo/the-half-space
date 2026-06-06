
package com.thehalfspace.entity;

import java.util.Arrays;
import java.util.function.Predicate;

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

    public static Competition fromFullName(String fullName) {
        return findBy(c -> c.fullName.equals(fullName), "league", fullName);
    }

    public static Competition fromCompetitionId(String competitionId) {
        return findBy(c -> c.competitionId.equals(competitionId), "competitionId", competitionId);
    }

    public static Competition fromCliCode(String cliCode) {
        return findBy(c -> c.cliCode.equals(cliCode), "CLI code", cliCode);
    }

    private static Competition findBy(Predicate<Competition> predicate, String label, String value) {
        return Arrays.stream(values())
                .filter(predicate)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown " + label + ": " + value));
    }
}