package com.thehalfspace.controller;

import com.thehalfspace.dto.ApiResponse;
import com.thehalfspace.dto.CommentRequest;
import com.thehalfspace.dto.CommentResponse;
import com.thehalfspace.security.CustomUserDetails;
import com.thehalfspace.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/matches/{matchId}/comments")
    public ApiResponse<Page<CommentResponse>> getMatchComments(
            @PathVariable Long matchId,
            Pageable pageable
    ) {
        return ApiResponse.of(commentService.getMatchComments(matchId, pageable));
    }

    @PostMapping("/matches/{matchId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CommentResponse> createMatchComment(
            @PathVariable Long matchId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid CommentRequest request
    ) {
        return ApiResponse.of(commentService.createMatchComment(matchId, userDetails.getUserId(), request));
    }

    @DeleteMapping("/comments/{id}")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        commentService.deleteComment(id, userDetails.getUserId());
        return ApiResponse.of(null);
    }
}
