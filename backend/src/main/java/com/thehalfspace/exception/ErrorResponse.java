package com.thehalfspace.exception;

import java.time.Instant;

public record ErrorResponse(String code, String message, Instant timestamp) {

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.name(), errorCode.getMessage(), Instant.now());
    }
}
