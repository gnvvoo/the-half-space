package com.thehalfspace.config;

import com.thehalfspace.entity.Match;
import com.thehalfspace.exception.AgentException;
import com.thehalfspace.service.AiContentService;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ReviewGenerationJobConfig {

    private static final int CHUNK_SIZE = 1;
    private static final long TOOL_CALL_DELAY_MS = 6000;

    private final EntityManagerFactory entityManagerFactory;
    private final AiContentService aiContentService;

    @Bean
    public Job reviewGenerationJob(JobRepository jobRepository, Step reviewGenerationStep) {
        return new JobBuilder("reviewGenerationJob", jobRepository)
                .start(reviewGenerationStep)
                .build();
    }

    @Bean
    public Step reviewGenerationStep(JobRepository jobRepository,
                                      PlatformTransactionManager transactionManager,
                                      JpaPagingItemReader<Match> reviewGenerationReader) {
        return new StepBuilder("reviewGenerationStep", jobRepository)
                .<Match, Match>chunk(CHUNK_SIZE, transactionManager)
                .reader(reviewGenerationReader)
                .processor(reviewGenerationProcessor())
                .writer(reviewGenerationWriter())
                .faultTolerant()
                .retryLimit(2)
                .retry(AgentException.class)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Match> reviewGenerationReader() {
        return new JpaPagingItemReaderBuilder<Match>()
                .name("reviewGenerationReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        SELECT m FROM Match m
                        WHERE m.status = 'FINISHED'
                        AND NOT EXISTS (
                            SELECT 1 FROM AiContent a
                            WHERE a.match.id = m.id AND a.type = 'REVIEW'
                        )
                        AND NOT EXISTS (
                            SELECT 1 FROM AgentExecution e
                            WHERE e.match.id = m.id AND e.status = 'IN_PROGRESS'
                        )
                        ORDER BY m.utcDate DESC
                        """)
                .pageSize(CHUNK_SIZE)
                .build();
    }

    private ItemProcessor<Match, Match> reviewGenerationProcessor() {
        return match -> {
            aiContentService.generateReview(match.getId());
            Thread.sleep(TOOL_CALL_DELAY_MS);
            return match;
        };
    }

    private ItemWriter<Match> reviewGenerationWriter() {
        return chunk -> chunk.forEach(match -> log.info("Review 생성 완료 - matchId: {}", match.getId()));
    }
}
