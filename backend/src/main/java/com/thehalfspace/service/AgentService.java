package com.thehalfspace.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thehalfspace.dto.request.AgentRequest;
import com.thehalfspace.dto.response.AgentResponse;
import com.thehalfspace.exception.AgentException;
import com.thehalfspace.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService {

    private static final int TIMEOUT_SECONDS = 120;

    @Value("${app.agent.binary-path:./bin/football-agent}")
    private String binaryPath;

    private final ObjectMapper objectMapper;

    public AgentResponse runAgent(AgentRequest request) {
        List<String> command = List.of(
                binaryPath,
                "--type", request.type().name(),
                "--match", String.valueOf(request.matchId()),
                "--output", "json"
        );

        log.info("AI 에이전트 실행: {}", command);

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(false);

            Process process = pb.start();

            String stdout = new String(process.getInputStream().readAllBytes());
            String stderr = new String(process.getErrorStream().readAllBytes());

            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                log.error("AI 에이전트 타임아웃 - command: {}", command);
                throw new AgentException(ErrorCode.AGENT_EXECUTION_FAILED);
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                log.error("AI 에이전트 비정상 종료 - exitCode: {}, stderr: {}", exitCode, stderr);
                throw new AgentException(ErrorCode.AGENT_EXECUTION_FAILED);
            }

            return objectMapper.readValue(stdout, AgentResponse.class);

        } catch (IOException | InterruptedException e) {
            log.error("AI 에이전트 실행 실패: {}", e.getMessage());
            throw new AgentException(ErrorCode.AGENT_EXECUTION_FAILED);
        }
    }
}
