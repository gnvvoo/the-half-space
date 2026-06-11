package com.thehalfspace.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        long expiresIn
) {}
