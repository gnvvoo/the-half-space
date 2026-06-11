package com.thehalfspace.controller;

import com.thehalfspace.dto.ApiResponse;
import com.thehalfspace.dto.AuthResponse;
import com.thehalfspace.dto.LoginRequest;
import com.thehalfspace.dto.RegisterRequest;
import com.thehalfspace.dto.TokenRefreshRequest;
import com.thehalfspace.security.CustomUserDetails;
import com.thehalfspace.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ApiResponse.of(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.of(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@RequestBody @Valid TokenRefreshRequest request) {
        return ApiResponse.of(authService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ApiResponse<Map<String, String>> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logout(userDetails.getUserId());
        return ApiResponse.of(Map.of("message", "로그아웃 성공"));
    }
}
