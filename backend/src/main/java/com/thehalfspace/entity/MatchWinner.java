package com.thehalfspace.entity;

public enum MatchWinner {
    HOME_TEAM, AWAY_TEAM, DRAW;

    public static MatchWinner from(String value) {
        if (value == null) {
            return null;
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("알 수 없는 경기 결과: " + value);
        }
    }
}
