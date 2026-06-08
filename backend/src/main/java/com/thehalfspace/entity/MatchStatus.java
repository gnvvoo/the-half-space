package com.thehalfspace.entity;

public enum MatchStatus {
    SCHEDULED, TIMED, LIVE, FINISHED, POSTPONED;

    public static MatchStatus from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("경기 상태 값이 null입니다");
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("알 수 없는 경기 상태: " + value);
        }
    }
}
