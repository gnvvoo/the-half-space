package com.thehalfspace.exception;

public class AgentException extends BusinessException {

    public AgentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
