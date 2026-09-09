package com.thehalfspace.batch;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Preview/Review 생성 배치 잡의 성공/실패를 Micrometer 카운터로 기록한다.
 * halfspace_batch_job_result_total{job="preview|review", result="success|failure"}
 */
@Component
@RequiredArgsConstructor
public class BatchMetricsListener implements JobExecutionListener {

    private static final String METRIC_NAME = "halfspace_batch_job_result_total";

    private final MeterRegistry meterRegistry;

    @Override
    public void afterJob(JobExecution jobExecution) {
        String job = resolveJobTag(jobExecution.getJobInstance().getJobName());
        String result = jobExecution.getStatus() == BatchStatus.COMPLETED ? "success" : "failure";
        meterRegistry.counter(METRIC_NAME, "job", job, "result", result).increment();
    }

    private String resolveJobTag(String jobName) {
        if (jobName.startsWith("preview")) return "preview";
        if (jobName.startsWith("review")) return "review";
        return jobName;
    }
}
