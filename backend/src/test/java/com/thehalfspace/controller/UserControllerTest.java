package com.thehalfspace.controller;

import com.thehalfspace.dto.ApiResponse;
import com.thehalfspace.dto.UserResponse;
import com.thehalfspace.entity.User;
import com.thehalfspace.entity.UserRole;
import com.thehalfspace.security.CustomUserDetails;
import com.thehalfspace.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);

        userController = new UserController(userService);
    }

    private CustomUserDetails userDetailsWithId(Long id) {
        User user = User.ofLocal("user" + id + "@test.com", "user" + id, "hash");
        ReflectionTestUtils.setField(user, "id", id);
        return new CustomUserDetails(user);
    }

    @Test
    void getMe_인증된_사용자의_프로필을_반환한다() {
        UserResponse expected = new UserResponse(1L, "user1@test.com", "user1", UserRole.USER, Instant.now());
        when(userService.getMyProfile(1L)).thenReturn(expected);

        ApiResponse<UserResponse> response = userController.getMe(userDetailsWithId(1L));

        assertThat(response.data()).isEqualTo(expected);
    }
}
