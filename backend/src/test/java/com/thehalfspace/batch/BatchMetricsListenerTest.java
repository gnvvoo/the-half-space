package com.thehalfspace.batch;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BatchMetricsListenerTest {

    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
    private final BatchMetricsListener listener = new BatchMetricsListener(meterRegistry);

    @Test
    void afterJob_preview_성공시_success_카운터_증가() {
        JobExecution jobExecution = jobExecution("previewGenerationJob", BatchStatus.COMPLETED);

        listener.afterJob(jobExecution);

        assertThat(counter("preview", "success")).isEqualTo(1.0);
    }

    @Test
    void afterJob_review_실패시_failure_카운터_증가() {
        JobExecution jobExecution = jobExecution("reviewGenerationJob", BatchStatus.FAILED);

        listener.afterJob(jobExecution);

        assertThat(counter("review", "failure")).isEqualTo(1.0);
    }

    @Test
    void afterJob_반복_호출시_카운터가_누적된다() {
        JobExecution jobExecution = jobExecution("previewGenerationJob", BatchStatus.COMPLETED);

        listener.afterJob(jobExecution);
        listener.afterJob(jobExecution);

        assertThat(counter("preview", "success")).isEqualTo(2.0);
    }

    private JobExecution jobExecution(String jobName, BatchStatus status) {
        JobExecution jobExecution = mock(JobExecution.class);
        JobInstance jobInstance = mock(JobInstance.class);
        when(jobExecution.getJobInstance()).thenReturn(jobInstance);
        when(jobInstance.getJobName()).thenReturn(jobName);
        when(jobExecution.getStatus()).thenReturn(status);
        return jobExecution;
    }

    private double counter(String job, String result) {
        return meterRegistry.get("halfspace_batch_job_result_total")
                .tag("job", job)
                .tag("result", result)
                .counter()
                .count();
    }
}
