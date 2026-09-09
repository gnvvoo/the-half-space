package com.thehalfspace.controller;

import com.thehalfspace.dto.ApiResponse;
import com.thehalfspace.dto.UserResponse;
import com.thehalfspace.security.CustomUserDetails;
import com.thehalfspace.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.of(userService.getMyProfile(userDetails.getUserId()));
    }
}
