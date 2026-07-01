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
public class ReviewGenerationScheduler {

    private final JobLauncher jobLauncher;
    private final Job reviewGenerationJob;

    @Scheduled(cron = "0 */5 * * * *") // 5분 주기
    public void run() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(reviewGenerationJob, params);
            log.info("ReviewGenerationJob 실행 완료");
        } catch (Exception e) {
            log.error("ReviewGenerationJob 실행 실패: {}", e.getMessage());
        }
    }
}
