package com.thehalfspace.dto;

import com.thehalfspace.entity.Board;

public record BoardResponse(
        Long id,
        String code,
        String name,
        String description
) {
    public static BoardResponse from(Board board) {
        return new BoardResponse(board.getId(), board.getCode(), board.getName(), board.getDescription());
    }
}
