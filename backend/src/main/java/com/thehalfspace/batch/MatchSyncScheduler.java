package com.thehalfspace.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchSyncScheduler {

    private final JobLauncher jobLauncher;
    private final Job matchSyncJob;

    @Scheduled(fixedDelay = 600_000) // 10분
    public void run() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(matchSyncJob, params);
            log.info("MatchSyncJob 실행 완료");
        } catch (Exception e) {
            log.error("MatchSyncJob 실행 실패: {}", e.getMessage());
        }
    }
}
