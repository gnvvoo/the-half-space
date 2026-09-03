package com.thehalfspace.dto;

import com.thehalfspace.entity.Post;

public record PostResponse(
        Long id,
        Long boardId,
        String boardCode,
        Long authorId,
        String authorNickname,
        String title,
        String content,
        int viewCount,
        boolean deleted,
        String createdAt,
        String updatedAt
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getBoard().getId(),
                post.getBoard().getCode(),
                post.getAuthor().getId(),
                post.getAuthor().getNickname(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.isDeleted(),
                post.getCreatedAt().toString(),
                post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : null
        );
    }
}
