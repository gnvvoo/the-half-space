package com.thehalfspace.config;

import com.thehalfspace.batch.MatchSyncTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    public Job matchSyncJob(JobRepository jobRepository, Step matchSyncStep) {
        return new JobBuilder("matchSyncJob", jobRepository)
                .start(matchSyncStep)
                .build();
    }

    @Bean
    public Step matchSyncStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               MatchSyncTasklet tasklet) {
        return new StepBuilder("matchSyncStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }
}
