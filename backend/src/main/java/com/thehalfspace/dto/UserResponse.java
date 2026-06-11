package com.thehalfspace.dto;

import com.thehalfspace.entity.User;
import com.thehalfspace.entity.UserRole;

import java.time.Instant;

public record UserResponse(
        Long id,
        String email,
        String nickname,
        UserRole role,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
