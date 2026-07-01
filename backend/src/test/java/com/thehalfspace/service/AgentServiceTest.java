package com.thehalfspace.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thehalfspace.dto.request.AgentRequest;
import com.thehalfspace.dto.response.AgentResponse;
import com.thehalfspace.entity.ContentType;
import com.thehalfspace.exception.AgentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

class AgentServiceTest {

    private AgentService agentService;

    @BeforeEach
    void setUp() {
        agentService = new AgentService(new ObjectMapper());
        ReflectionTestUtils.setField(agentService, "binaryPath", "./bin/football-agent");
    }

    @Test
    void runAgent_정상_실행_시_AgentResponse를_반환한다() throws Exception {
        String json = """
                {"body":"본문","summary":"요약","reasoning":null,"tokenUsed":100,
                 "modelVersion":"gemini-2.5","toolsCalled":null,"prediction":null}
                """;

        Process mockProcess = mock(Process.class);
        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        when(mockProcess.getErrorStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(mockProcess.waitFor(120, TimeUnit.SECONDS)).thenReturn(true);
        when(mockProcess.exitValue()).thenReturn(0);

        try (MockedConstruction<ProcessBuilder> mocked = mockConstruction(ProcessBuilder.class,
                (mock, context) -> when(mock.start()).thenReturn(mockProcess))) {

            AgentResponse response = agentService.runAgent(new AgentRequest(ContentType.PREVIEW, 1L));

            assertThat(response.body()).isEqualTo("본문");
            assertThat(response.modelVersion()).isEqualTo("gemini-2.5");
        }
    }

    @Test
    void runAgent_타임아웃_시_AgentException을_던진다() throws Exception {
        Process mockProcess = mock(Process.class);
        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(mockProcess.getErrorStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(mockProcess.waitFor(120, TimeUnit.SECONDS)).thenReturn(false);

        try (MockedConstruction<ProcessBuilder> mocked = mockConstruction(ProcessBuilder.class,
                (mock, context) -> when(mock.start()).thenReturn(mockProcess))) {

            assertThatThrownBy(() -> agentService.runAgent(new AgentRequest(ContentType.PREVIEW, 1L)))
                    .isInstanceOf(AgentException.class);
        }
    }

    @Test
    void runAgent_비정상_종료_시_AgentException을_던진다() throws Exception {
        Process mockProcess = mock(Process.class);
        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(mockProcess.getErrorStream()).thenReturn(new ByteArrayInputStream("error".getBytes(StandardCharsets.UTF_8)));
        when(mockProcess.waitFor(120, TimeUnit.SECONDS)).thenReturn(true);
        when(mockProcess.exitValue()).thenReturn(1);

        try (MockedConstruction<ProcessBuilder> mocked = mockConstruction(ProcessBuilder.class,
                (mock, context) -> when(mock.start()).thenReturn(mockProcess))) {

            assertThatThrownBy(() -> agentService.runAgent(new AgentRequest(ContentType.REVIEW, 2L)))
                    .isInstanceOf(AgentException.class);
        }
    }
}
