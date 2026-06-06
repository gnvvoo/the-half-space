package com.thehalfspace.controller;

import com.thehalfspace.dto.MatchResponse;
import com.thehalfspace.dto.StandingResponse;
import com.thehalfspace.service.MatchService;
import com.thehalfspace.service.StandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final StandingService standingService;

    @GetMapping("/matches")
    public List<MatchResponse> getMatches(
            @RequestParam String competition,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return matchService.getMatches(competition, from, to);
    }

    @GetMapping("/matches/{id}")
    public MatchResponse getMatch(@PathVariable Long id) {
        return matchService.getMatch(id);
    }

    @GetMapping("/standings")
    public List<StandingResponse> getStandings(@RequestParam String competition) {
        return standingService.getStandings(competition);
    }
}
