package com.thehalfspace.controller;

import com.thehalfspace.dto.StandingResponse;
import com.thehalfspace.service.StandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StandingController {

    private final StandingService standingService;

    @GetMapping("/standings")
    public List<StandingResponse> getStandings(@RequestParam String competition) {
        return standingService.getStandings(competition);
    }
}
