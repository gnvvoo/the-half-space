package com.thehalfspace.service;

import com.thehalfspace.dto.request.AgentRequest;
import com.thehalfspace.dto.response.AgentResponse;
import com.thehalfspace.dto.response.AiContentResponse;
import com.thehalfspace.dto.response.AiPredictionResponse;
import com.thehalfspace.dto.response.MatchStatsResponse;
import com.thehalfspace.entity.*;
import com.thehalfspace.exception.BusinessException;
import com.thehalfspace.exception.NotFoundException;
import com.thehalfspace.repository.AgentExecutionRepository;
import com.thehalfspace.repository.AiContentRepository;
import com.thehalfspace.repository.AiPredictionRepository;
import com.thehalfspace.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AiContentServiceTest {

    private MatchRepository matchRepository;
    private AiContentRepository aiContentRepository;
    private AiPredictionRepository aiPredictionRepository;
    private AgentExecutionRepository agentExecutionRepository;
    private AgentService agentService;
    private RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> valueOperations;
    private HashOperations<String, Object, Object> hashOperations;

    private AiContentService aiContentService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        matchRepository = mock(MatchRepository.class);
        aiContentRepository = mock(AiContentRepository.class);
        aiPredictionRepository = mock(AiPredictionRepository.class);
        agentExecutionRepository = mock(AgentExecutionRepository.class);
        agentService = mock(AgentService.class);
        redisTemplate = mock(RedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        hashOperations = mock(HashOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        aiContentService = new AiContentService(
                matchRepository, aiContentRepository, aiPredictionRepository,
                agentExecutionRepository, agentService, redisTemplate
        );
    }

    @Test
    void generatePreview_이미_실행중이면_AGENT_ALREADY_RUNNING_예외() {
        when(agentExecutionRepository.existsByMatchIdAndStatus(1L, ExecutionStatus.IN_PROGRESS)).thenReturn(true);

        assertThatThrownBy(() -> aiContentService.generatePreview(1L))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(agentService);
    }

    @Test
    void generatePreview_이미_생성된_콘텐츠가_있으면_기존_콘텐츠를_반환한다() {
        Match match = Match.builder().id(1L).build();
        AiContent existing = AiContent.of(match, ContentType.PREVIEW, "기존 본문", "기존 요약", null, 10, "gemini-2.5");

        when(agentExecutionRepository.existsByMatchIdAndStatus(1L, ExecutionStatus.IN_PROGRESS)).thenReturn(false);
        when(aiContentRepository.findByMatchIdAndType(1L, ContentType.PREVIEW)).thenReturn(Optional.of(existing));

        AiContentResponse response = aiContentService.generatePreview(1L);

        assertThat(response.body()).isEqualTo("기존 본문");
        verifyNoInteractions(agentService);
    }

    @Test
    void generatePreview_정상_실행_시_저장_및_캐싱한다() {
        Match match = Match.builder().id(1L).build();
        AgentResponse.PredictionResult prediction =
                new AgentResponse.PredictionResult(60.0, 20.0, 20.0, "2:1");
        AgentResponse agentResponse = new AgentResponse(
                "preview", 1L, "본문", "요약", null, prediction, null, 100, "gemini-2.5"
        );

        when(agentExecutionRepository.existsByMatchIdAndStatus(1L, ExecutionStatus.IN_PROGRESS)).thenReturn(false);
        when(aiContentRepository.findByMatchIdAndType(1L, ContentType.PREVIEW)).thenReturn(Optional.empty());
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(agentExecutionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(agentService.runAgent(new AgentRequest("preview", 1L))).thenReturn(agentResponse);
        when(aiContentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AiContentResponse response = aiContentService.generatePreview(1L);

        assertThat(response.body()).isEqualTo("본문");
        verify(aiPredictionRepository).save(any());
        verify(valueOperations).set(eq("ai:preview:1"), any(), any());
    }

    @Test
    void getPreview_캐시_히트_시_저장소_조회_없이_반환한다() {
        AiContentResponse cached = new AiContentResponse(null, 1L, "PREVIEW", "캐시된 본문", "요약", true, null);
        when(valueOperations.get("ai:preview:1")).thenReturn(cached);

        AiContentResponse response = aiContentService.getPreview(1L);

        assertThat(response.body()).isEqualTo("캐시된 본문");
        verifyNoInteractions(aiContentRepository);
    }

    @Test
    void getPreview_캐시_미스_및_미발행_시_PREVIEW_NOT_READY_예외() {
        when(valueOperations.get("ai:preview:1")).thenReturn(null);
        when(aiContentRepository.findByMatchIdAndTypeAndIsPublished(1L, ContentType.PREVIEW, true))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> aiContentService.getPreview(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getMatchStats_예측과_팬투표_분포를_합쳐서_반환한다() {
        AiPredictionResponse prediction = new AiPredictionResponse(1L, 60.0, 20.0, 20.0, "2:1", null);
        when(valueOperations.get("ai:pred:1")).thenReturn(prediction);
        when(hashOperations.entries("predict:dist:1")).thenReturn(Map.of(
                "HOME", "10", "DRAW", "3", "AWAY", "5"
        ));

        MatchStatsResponse stats = aiContentService.getMatchStats(1L);

        assertThat(stats.aiPrediction()).isEqualTo(prediction);
        assertThat(stats.fanDistribution().home()).isEqualTo(10L);
        assertThat(stats.fanDistribution().draw()).isEqualTo(3L);
        assertThat(stats.fanDistribution().away()).isEqualTo(5L);
    }

    @Test
    void getMatchStats_예측이_없으면_null로_채우고_팬투표만_반환한다() {
        when(valueOperations.get("ai:pred:1")).thenReturn(null);
        when(aiPredictionRepository.findByMatchId(1L)).thenReturn(Optional.empty());
        when(hashOperations.entries("predict:dist:1")).thenReturn(Map.of());

        MatchStatsResponse stats = aiContentService.getMatchStats(1L);

        assertThat(stats.aiPrediction()).isNull();
        assertThat(stats.fanDistribution().home()).isEqualTo(0L);
    }
}
