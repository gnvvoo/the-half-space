package com.thehalfspace.dto;

import com.thehalfspace.entity.Comment;

import java.util.List;

public record CommentResponse(
        Long id,
        Long matchId,
        Long postId,
        Long parentId,
        Long authorId,
        String authorNickname,
        String content,
        boolean deleted,
        String createdAt,
        List<CommentResponse> replies
) {
    public static CommentResponse from(Comment comment) {
        return from(comment, List.of());
    }

    public static CommentResponse from(Comment comment, List<CommentResponse> replies) {
        return new CommentResponse(
                comment.getId(),
                comment.getMatch() != null ? comment.getMatch().getId() : null,
                comment.getPost() != null ? comment.getPost().getId() : null,
                comment.getParent() != null ? comment.getParent().getId() : null,
                comment.getAuthor().getId(),
                comment.isDeleted() ? null : comment.getAuthor().getNickname(),
                comment.isDeleted() ? null : comment.getContent(),
                comment.isDeleted(),
                comment.getCreatedAt().toString(),
                replies
        );
    }
}
