package com.thehalfspace.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.thehalfspace.entity.Competition;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FootballApiClient {

    private static final String API_CALLS_METRIC_NAME = "halfspace_football_api_calls_total";

    private final RestClient footballRestClient;
    private final MeterRegistry meterRegistry;

    // football-data.org 호출 결과를 outcome(success/error) 태그로 카운팅한다.
    // 무료 티어 호출량 상한 감시용 (infra/prometheus/alert-rules.yml 참고).
    private void recordApiCall(String outcome) {
        meterRegistry.counter(API_CALLS_METRIC_NAME, "outcome", outcome).increment();
    }

    // --- Response DTOs ---

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TeamDto(Long id, String name, String shortName, String tla, String crest) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FullTimeDto(Integer home, Integer away) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ScoreDto(String winner, FullTimeDto fullTime) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MatchDto(Long id, String utcDate, String status, Integer matchday,
                           String venue, TeamDto homeTeam, TeamDto awayTeam, ScoreDto score) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MatchesResponse(List<MatchDto> matches) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StandingEntryDto(Integer position, TeamDto team,
                                   Integer playedGames, Integer won, Integer draw, Integer lost,
                                   Integer goalsFor, Integer goalsAgainst,
                                   Integer goalDifference, Integer points) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record StandingTableDto(String type, List<StandingEntryDto> table) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record StandingsResponse(List<StandingTableDto> standings) {}

    // --- API 호출 ---

    public List<MatchDto> fetchMatches(Competition competition, LocalDate from, LocalDate to) {
        try {
            MatchesResponse response = footballRestClient.get()
                    .uri("/competitions/{id}/matches?dateFrom={from}&dateTo={to}",
                            competition.getCompetitionId(), from, to)
                    .retrieve()
                    .body(MatchesResponse.class);
            recordApiCall("success");
            return response != null && response.matches() != null ? response.matches() : List.of();
        } catch (RestClientException e) {
            recordApiCall("error");
            log.error("경기 조회 실패 - {}: {}", competition.getCompetitionId(), e.getMessage());
            return List.of();
        }
    }

    public List<StandingEntryDto> fetchStandings(Competition competition) {
        try {
            StandingsResponse response = footballRestClient.get()
                    .uri("/competitions/{id}/standings", competition.getCompetitionId())
                    .retrieve()
                    .body(StandingsResponse.class);
            recordApiCall("success");

            if (response == null || response.standings() == null) return List.of();

            return response.standings().stream()
                    .filter(s -> "TOTAL".equals(s.type()))
                    .findFirst()
                    .map(StandingTableDto::table)
                    .orElse(List.of());
        } catch (RestClientException e) {
            recordApiCall("error");
            log.error("순위 조회 실패 - {}: {}", competition.getCompetitionId(), e.getMessage());
            return List.of();
        }
    }
}
