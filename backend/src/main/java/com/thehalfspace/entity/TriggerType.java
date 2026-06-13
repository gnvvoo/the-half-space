package com.thehalfspace.entity;

public enum TriggerType {
    SCHEDULED,   // 배치 스케줄
    MATCH_END,   // 경기 종료 감지
    USER_QUERY   // 사용자 요청
}
