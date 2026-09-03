package com.thehalfspace.controller;

import com.thehalfspace.dto.ApiResponse;
import com.thehalfspace.dto.CommentRequest;
import com.thehalfspace.dto.CommentResponse;
import com.thehalfspace.dto.PostRequest;
import com.thehalfspace.dto.PostResponse;
import com.thehalfspace.security.CustomUserDetails;
import com.thehalfspace.service.CommentService;
import com.thehalfspace.service.PostService;
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
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/boards/{boardId}/posts")
    public ApiResponse<Page<PostResponse>> getPosts(
            @PathVariable Long boardId,
            @RequestParam(required = false) String title,
            Pageable pageable
    ) {
        return ApiResponse.of(postService.getPosts(boardId, title, pageable));
    }

    @PostMapping("/boards/{boardId}/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostResponse> createPost(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid PostRequest request
    ) {
        return ApiResponse.of(postService.createPost(boardId, userDetails.getUserId(), request));
    }

    @GetMapping("/posts/{id}")
    public ApiResponse<PostResponse> getPost(@PathVariable Long id) {
        return ApiResponse.of(postService.getPost(id));
    }

    @PutMapping("/posts/{id}")
    public ApiResponse<PostResponse> updatePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid PostRequest request
    ) {
        return ApiResponse.of(postService.updatePost(id, userDetails.getUserId(), request));
    }

    @DeleteMapping("/posts/{id}")
    public ApiResponse<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        postService.deletePost(id, userDetails.getUserId());
        return ApiResponse.of(null);
    }

    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<Page<CommentResponse>> getPostComments(
            @PathVariable Long postId,
            Pageable pageable
    ) {
        return ApiResponse.of(commentService.getPostComments(postId, pageable));
    }

    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CommentResponse> createPostComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid CommentRequest request
    ) {
        return ApiResponse.of(commentService.createPostComment(postId, userDetails.getUserId(), request));
    }
}
