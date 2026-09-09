package com.thehalfspace.client;

import com.thehalfspace.entity.Competition;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FootballApiClientTest {

    @Test
    void fetchMatches_성공시_success_카운터가_증가한다() {
        RestClient restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        FootballApiClient client = new FootballApiClient(restClient, meterRegistry);

        List<FootballApiClient.MatchDto> result =
                client.fetchMatches(Competition.EPL, LocalDate.now(), LocalDate.now());

        assertThat(result).isEmpty();
        assertThat(counter(meterRegistry, "success")).isEqualTo(1.0);
        assertThat(counter(meterRegistry, "error")).isEqualTo(0.0);
    }

    @Test
    void fetchMatches_예외발생시_error_카운터가_증가한다() {
        RestClient restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        when(restClient.get()).thenThrow(new RestClientException("연결 실패"));
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        FootballApiClient client = new FootballApiClient(restClient, meterRegistry);

        List<FootballApiClient.MatchDto> result =
                client.fetchMatches(Competition.EPL, LocalDate.now(), LocalDate.now());

        assertThat(result).isEmpty();
        assertThat(counter(meterRegistry, "error")).isEqualTo(1.0);
        assertThat(counter(meterRegistry, "success")).isEqualTo(0.0);
    }

    @Test
    void fetchStandings_성공시_success_카운터가_증가한다() {
        RestClient restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        FootballApiClient client = new FootballApiClient(restClient, meterRegistry);

        List<FootballApiClient.StandingEntryDto> result = client.fetchStandings(Competition.EPL);

        assertThat(result).isEmpty();
        assertThat(counter(meterRegistry, "success")).isEqualTo(1.0);
    }

    @Test
    void fetchStandings_예외발생시_error_카운터가_증가한다() {
        RestClient restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        when(restClient.get()).thenThrow(new RestClientException("연결 실패"));
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        FootballApiClient client = new FootballApiClient(restClient, meterRegistry);

        List<FootballApiClient.StandingEntryDto> result = client.fetchStandings(Competition.EPL);

        assertThat(result).isEmpty();
        assertThat(counter(meterRegistry, "error")).isEqualTo(1.0);
    }

    private double counter(SimpleMeterRegistry meterRegistry, String outcome) {
        var counter = meterRegistry.find("halfspace_football_api_calls_total")
                .tag("outcome", outcome)
                .counter();
        return counter != null ? counter.count() : 0.0;
    }
}
