package com.thehalfspace.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public final class SeasonUtils {

    private SeasonUtils() {}

    public static String currentSeason() {
        return deriveSeason(LocalDate.now(ZoneOffset.UTC));
    }

    public static String deriveSeason(Instant instant) {
        return deriveSeason(instant.atZone(ZoneOffset.UTC).toLocalDate());
    }

    private static String deriveSeason(LocalDate date) {
        int startYear = date.getMonthValue() >= 8 ? date.getYear() : date.getYear() - 1;
        return startYear + "-" + String.format("%02d", (startYear + 1) % 100);
    }
}
