package com.thehalfspace.cli;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class CliRunnerTest {

    @Autowired
    CliRunner cliRunner;

    @Test
    void manifest_호출_테스트() throws Exception {
        var result = cliRunner.runJson("--manifest");
        log.info("manifest 응답: {}", result.toPrettyString());
    }
}