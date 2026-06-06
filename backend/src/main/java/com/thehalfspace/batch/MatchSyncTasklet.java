package com.thehalfspace.batch;

import com.thehalfspace.client.FootballApiClient;
import com.thehalfspace.entity.Competition;
import com.thehalfspace.entity.Match;
import com.thehalfspace.entity.Standing;
import com.thehalfspace.entity.Team;
import com.thehalfspace.repository.MatchRepository;
import com.thehalfspace.repository.StandingRepository;
import com.thehalfspace.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchSyncTasklet implements Tasklet {

    private final FootballApiClient apiClient;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final StandingRepository standingRepository;
    private final CacheManager cacheManager;

    private record DateRange(LocalDate from, LocalDate to) {}

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        for (Competition competition : Competition.values()) {
            try {
                syncCompetition(competition);
                syncStandings(competition);
            } catch (Exception e) {
                log.error("동기화 실패 - {}: {}", competition.getCompetitionId(), e.getMessage());
            }
        }
        evictCaches();
        return RepeatStatus.FINISHED;
    }

    private void syncCompetition(Competition competition) {
        DateRange range = getDateRange(competition);
        log.info("경기 동기화 시작 - {} ({} ~ {})",
                competition.getCompetitionId(), range.from(), range.to());

        var matches = apiClient.fetchMatches(competition, range.from(), range.to());
        if (matches.isEmpty()) {
            log.info("조회된 경기 없음 - {}", competition.getCompetitionId());
            return;
        }

        int saved = 0, updated = 0;

        for (var dto : matches) {
            Integer homeScore = dto.score().fullTime().home();
            Integer awayScore = dto.score().fullTime().away();
            Instant utcDate   = Instant.parse(dto.utcDate());

            Optional<Match> existing = matchRepository.findById(dto.id());
            if (existing.isPresent()) {
                existing.get().update(dto.status(), homeScore, awayScore, dto.score().winner());
                updated++;
            } else {
                Team homeTeam = resolveTeam(dto.homeTeam(), competition.getCompetitionId());
                Team awayTeam = resolveTeam(dto.awayTeam(), competition.getCompetitionId());

                matchRepository.save(Match.of(
                        dto.id(), competition.getCompetitionId(), deriveSeason(utcDate),
                        homeTeam, awayTeam, dto.status(), dto.matchday(), utcDate,
                        homeScore, awayScore, dto.score().winner(),
                        (dto.venue() != null && !dto.venue().isBlank()) ? dto.venue() : null
                ));
                saved++;
            }
        }

        log.info("동기화 완료 - {} | 저장: {}, 업데이트: {}",
                competition.getCompetitionId(), saved, updated);
    }

    // DB가 비어있으면 시즌 전체, 아니면 최근 3일 + 향후 7일
    private DateRange getDateRange(Competition competition) {
        if (matchRepository.countByCompetitionId(competition.getCompetitionId()) == 0) {
            LocalDate now = LocalDate.now(ZoneOffset.UTC);
            int startYear = now.getMonthValue() >= 8 ? now.getYear() : now.getYear() - 1;
            return new DateRange(
                    LocalDate.of(startYear, 8, 1),
                    LocalDate.of(startYear + 1, 6, 1)
            );
        }
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        return new DateRange(today.minusDays(3), today.plusDays(7));
    }

    private Team resolveTeam(FootballApiClient.TeamDto dto, String competitionId) {
        return teamRepository.findById(dto.id()).map(team -> {
            team.update(dto.name(), dto.shortName(), dto.tla(), dto.crest());
            return teamRepository.save(team);
        }).orElseGet(() -> teamRepository.save(
                Team.of(dto.id(), dto.name(), dto.shortName(), dto.tla(), dto.crest(), competitionId)
        ));
    }

    private void evictCaches() {
        var matchesCache = cacheManager.getCache("matches");
        var standingsCache = cacheManager.getCache("standings");
        if (matchesCache != null) matchesCache.clear();
        if (standingsCache != null) standingsCache.clear();
        log.info("캐시 무효화 완료");
    }

    private void syncStandings(Competition competition) {
        var entries = apiClient.fetchStandings(competition);
        if (entries.isEmpty()) {
            log.info("순위 데이터 없음 - {}", competition.getCompetitionId());
            return;
        }

        String season = currentSeason();
        standingRepository.deleteByCompetitionIdAndSeason(competition.getCompetitionId(), season);

        for (var entry : entries) {
            Team team = resolveTeam(entry.team(), competition.getCompetitionId());
            standingRepository.save(Standing.of(
                    competition.getCompetitionId(), season, team,
                    entry.position(), entry.playedGames(),
                    entry.won(), entry.draw(), entry.lost(),
                    entry.goalsFor(), entry.goalsAgainst(),
                    entry.goalDifference(), entry.points()
            ));
        }

        log.info("순위 동기화 완료 - {} ({} 팀)", competition.getCompetitionId(), entries.size());
    }

    private String currentSeason() {
        LocalDate now = LocalDate.now(ZoneOffset.UTC);
        int startYear = now.getMonthValue() >= 8 ? now.getYear() : now.getYear() - 1;
        return startYear + "-" + String.format("%02d", (startYear + 1) % 100);
    }

    private String deriveSeason(Instant utcDate) {
        LocalDate date = utcDate.atZone(ZoneOffset.UTC).toLocalDate();
        int year = date.getYear();
        int startYear = date.getMonthValue() >= 8 ? year : year - 1;
        return startYear + "-" + String.format("%02d", (startYear + 1) % 100);
    }
}
