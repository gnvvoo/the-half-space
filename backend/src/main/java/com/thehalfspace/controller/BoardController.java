package com.thehalfspace.controller;

import com.thehalfspace.dto.ApiResponse;
import com.thehalfspace.dto.BoardResponse;
import com.thehalfspace.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/boards")
    public ApiResponse<List<BoardResponse>> getBoards() {
        return ApiResponse.of(boardService.getBoards());
    }
}
