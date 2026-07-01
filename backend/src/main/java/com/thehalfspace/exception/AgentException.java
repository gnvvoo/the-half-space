package com.thehalfspace.exception;

public class AgentException extends BusinessException {

    public AgentException(String message) {
        super(ErrorCode.AGENT_EXECUTION_FAILED, message);
    }
}
