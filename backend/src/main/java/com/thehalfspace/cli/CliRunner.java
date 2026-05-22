package com.thehalfspace.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CliRunner {

    @Value("${cli.path:./cli/football-cli}")
    private String cliPath;

    // CLI 실행 결과 보관
    public record CliResult(String stdout, String stderr, int exitCode) {
        public boolean isSuccess() { return exitCode == 0; }
        public boolean isNoData()  { return exitCode == 3; }
        public boolean isApiFail() { return exitCode == 4; }
    }

    // CLI 실행
    public CliResult run(String... args) {
        List<String> command = new ArrayList<>();
        command.add(cliPath);
        command.addAll(Arrays.asList(args));

        log.info("CLI 실행: {}", command);

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(false);

            Process process = pb.start();

            // stdout, stderr 읽기
            String stdout = new String(process.getInputStream().readAllBytes());
            String stderr = new String(process.getErrorStream().readAllBytes());

            // 10초 타임아웃
            boolean finished = process.waitFor(10, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                log.error("CLI 타임아웃");
                return new CliResult("", "timeout", -1);
            }

            int exitCode = process.exitValue();
            log.info("CLI 종료 코드: {}", exitCode);

            return new CliResult(stdout, stderr, exitCode);

        } catch (IOException | InterruptedException e) {
            log.error("CLI 실행 실패: {}", e.getMessage());
            return new CliResult("", e.getMessage(), -1);
        }
    }

    // JSON으로 파싱해서 반환
    public JsonNode runJson(String... args) throws IOException {
        // --json --no-color --quiet 자동 추가
        List<String> fullArgs = new ArrayList<>(Arrays.asList(args));
        fullArgs.addAll(List.of("--json", "--no-color", "--quiet"));

        CliResult result = run(fullArgs.toArray(new String[0]));

        if (!result.isSuccess()) {
            log.warn("CLI 비정상 종료 - exitCode: {}, stderr: {}", result.exitCode(), result.stderr());
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(result.stdout());
    }
}