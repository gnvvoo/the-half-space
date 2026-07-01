package com.thehalfspace.service;

import com.thehalfspace.dto.request.AgentRequest;
import com.thehalfspace.dto.response.AgentResponse;
import com.thehalfspace.dto.response.AiContentResponse;
import com.thehalfspace.dto.response.AiPredictionResponse;
import com.thehalfspace.entity.*;
import com.thehalfspace.exception.BusinessException;
import com.thehalfspace.exception.ErrorCode;
import com.thehalfspace.exception.NotFoundException;
import com.thehalfspace.repository.AgentExecutionRepository;
import com.thehalfspace.repository.AiContentRepository;
import com.thehalfspace.repository.AiPredictionRepository;
import com.thehalfspace.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AiContentService {

    private static final Duration PREVIEW_TTL = Duration.ofHours(1);
    private static final Duration REVIEW_TTL = Duration.ofDays(7);
    private static final Duration PREDICTION_TTL = Duration.ofHours(24);

    private final MatchRepository matchRepository;
    private final AiContentRepository aiContentRepository;
    private final AiPredictionRepository aiPredictionRepository;
    private final AgentExecutionRepository agentExecutionRepository;
    private final AgentService agentService;
    private final RedisTemplate<String, Object> redisTemplate;

    public AiContentResponse generatePreview(Long matchId) {
        return generate(matchId, ContentType.PREVIEW, "preview", "ai:preview:" + matchId, PREVIEW_TTL);
    }

    public AiContentResponse generateReview(Long matchId) {
        return generate(matchId, ContentType.REVIEW, "review", "ai:review:" + matchId, REVIEW_TTL);
    }

    @Transactional(readOnly = true)
    public AiContentResponse getPreview(Long matchId) {
        return getContent(matchId, ContentType.PREVIEW, "ai:preview:" + matchId, PREVIEW_TTL, ErrorCode.PREVIEW_NOT_READY);
    }

    @Transactional(readOnly = true)
    public AiContentResponse getReview(Long matchId) {
        return getContent(matchId, ContentType.REVIEW, "ai:review:" + matchId, REVIEW_TTL, ErrorCode.REVIEW_NOT_READY);
    }

    @Transactional(readOnly = true)
    public AiPredictionResponse getAiPrediction(Long matchId) {
        String cacheKey = "ai:pred:" + matchId;

        AiPredictionResponse cached = (AiPredictionResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        AiPredictionResponse response = aiPredictionRepository.findByMatchId(matchId)
                .map(AiPredictionResponse::from)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PREVIEW_NOT_READY));

        redisTemplate.opsForValue().set(cacheKey, response, PREDICTION_TTL);
        return response;
    }

    private AiContentResponse generate(Long matchId, ContentType contentType, String agentType,
                                        String cacheKey, Duration ttl) {
        if (agentExecutionRepository.existsByMatchIdAndStatus(matchId, ExecutionStatus.IN_PROGRESS)) {
            throw new BusinessException(ErrorCode.AGENT_ALREADY_RUNNING);
        }

        var existing = aiContentRepository.findByMatchIdAndType(matchId, contentType);
        if (existing.isPresent()) {
            return AiContentResponse.from(existing.get());
        }

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MATCH_NOT_FOUND));

        AgentExecution execution = agentExecutionRepository.save(AgentExecution.of(match, TriggerType.SCHEDULED));

        long startedAt = System.currentTimeMillis();
        AgentResponse agentResponse;
        try {
            agentResponse = agentService.runAgent(new AgentRequest(agentType, matchId));
        } catch (RuntimeException e) {
            execution.fail(e.getMessage());
            throw e;
        }
        int latencyMs = (int) (System.currentTimeMillis() - startedAt);

        AiContent aiContent = aiContentRepository.save(AiContent.of(
                match, contentType, agentResponse.body(), agentResponse.summary(),
                agentResponse.reasoning(), agentResponse.totalTokens(), agentResponse.modelVersion()
        ));

        if (agentResponse.prediction() != null) {
            var prediction = agentResponse.prediction();
            aiPredictionRepository.save(AiPrediction.of(
                    match, prediction.homeWinPct(), prediction.drawPct(), prediction.awayWinPct(),
                    prediction.predictedScore(), agentResponse.reasoning()
            ));
        }

        execution.complete(agentResponse.totalTokens(), latencyMs);
        aiContent.publish();

        AiContentResponse response = AiContentResponse.from(aiContent);
        redisTemplate.opsForValue().set(cacheKey, response, ttl);
        return response;
    }

    private AiContentResponse getContent(Long matchId, ContentType contentType, String cacheKey,
                                          Duration ttl, ErrorCode notReadyCode) {
        AiContentResponse cached = (AiContentResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        AiContentResponse response = aiContentRepository.findByMatchIdAndTypeAndIsPublished(matchId, contentType, true)
                .map(AiContentResponse::from)
                .orElseThrow(() -> new NotFoundException(notReadyCode));

        redisTemplate.opsForValue().set(cacheKey, response, ttl);
        return response;
    }
}
