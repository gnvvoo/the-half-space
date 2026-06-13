package com.thehalfspace.entity;

public enum ExecutionStatus {
    IN_PROGRESS,  // 생성 중 (중복 실행 방지용)
    SUCCESS,
    FAILED,
    PARTIAL
}
