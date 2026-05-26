package com.thehalfspace.batch;

import com.fasterxml.jackson.databind.JsonNode;
import com.thehalfspace.cli.CliRunner;
import com.thehalfspace.entity.Competition;
import com.thehalfspace.entity.Match;
import com.thehalfspace.entity.Team;
import com.thehalfspace.repository.MatchRepository;
import com.thehalfspace.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchSyncTasklet implements Tasklet {

    private final CliRunner cliRunner;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        for (Competition competition : Competition.values()) {
            try {
                syncCompetition(competition);
            } catch (Exception e) {
                log.error("경기 동기화 실패 - competition: {}, error: {}", competition.getCliCode(), e.getMessage());
            }
        }
        return RepeatStatus.FINISHED;
    }

    private void syncCompetition(Competition competition) throws Exception {
        log.info("경기 동기화 시작 - {}", competition.getCliCode());

        JsonNode root = cliRunner.runJson("matches", "--league", competition.getCliCode());
        JsonNode matches = root.get("matches");

        if (matches == null || !matches.isArray()) {
            log.warn("경기 데이터 없음 - {}", competition.getCliCode());
            return;
        }

        int saved = 0, updated = 0;

        for (JsonNode node : matches) {
            long matchId       = node.get("id").asLong();
            String status      = node.get("status").asText();
            Instant utcDate    = Instant.parse(node.get("date").asText());
            String homeTeamName = node.get("home_team").asText();
            String awayTeamName = node.get("away_team").asText();
            String venue       = node.get("venue").asText(null);

            JsonNode score     = node.get("score");
            Integer homeScore  = score.get("home").isNull() ? null : score.get("home").asInt();
            Integer awayScore  = score.get("away").isNull() ? null : score.get("away").asInt();
            String winner      = deriveWinner(status, homeScore, awayScore);

            Optional<Match> existing = matchRepository.findById(matchId);
            if (existing.isPresent()) {
                existing.get().update(status, homeScore, awayScore, winner);
                matchRepository.save(existing.get());
                updated++;
            } else {
                Team homeTeam = resolveTeam(homeTeamName, competition.getCompetitionId());
                Team awayTeam = resolveTeam(awayTeamName, competition.getCompetitionId());
                String season = deriveSeason(utcDate);

                Match match = Match.of(
                        matchId, competition.getCompetitionId(), season,
                        homeTeam, awayTeam, status, utcDate,
                        homeScore, awayScore, winner,
                        (venue != null && !venue.isBlank()) ? venue : null
                );
                matchRepository.save(match);
                saved++;
            }
        }

        log.info("동기화 완료 - {} | 저장: {}, 업데이트: {}", competition.getCliCode(), saved, updated);
    }

    // 팀명으로 조회, 없으면 해시 기반 id로 신규 생성
    private Team resolveTeam(String name, String competitionId) {
        return teamRepository.findByName(name).orElseGet(() -> {
            long syntheticId = name.hashCode() & 0xFFFFFFFFL;
            return teamRepository.save(Team.of(syntheticId, name, competitionId));
        });
    }

    // 날짜로 시즌 문자열 계산 (8월 이후 = 새 시즌)
    private String deriveSeason(Instant utcDate) {
        LocalDate date = utcDate.atZone(ZoneOffset.UTC).toLocalDate();
        int year = date.getYear();
        int startYear = date.getMonthValue() >= 8 ? year : year - 1;
        return startYear + "-" + String.format("%02d", (startYear + 1) % 100);
    }

    private String deriveWinner(String status, Integer homeScore, Integer awayScore) {
        if (!"FINISHED".equals(status) || homeScore == null || awayScore == null) return null;
        if (homeScore > awayScore) return "HOME_TEAM";
        if (homeScore < awayScore) return "AWAY_TEAM";
        return "DRAW";
    }
}
