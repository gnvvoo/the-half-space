package com.thehalfspace.service;

import com.thehalfspace.dto.UserResponse;
import com.thehalfspace.entity.User;
import com.thehalfspace.exception.NotFoundException;
import com.thehalfspace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);

        userService = new UserService(userRepository);
    }

    @Test
    void getMyProfile_인증된_사용자면_프로필을_반환한다() {
        User user = User.ofLocal("user1@test.com", "user1", "hash");
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.getMyProfile(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("user1@test.com");
        assertThat(response.nickname()).isEqualTo("user1");
        assertThat(response.createdAt()).isEqualTo(user.getCreatedAt());
    }

    @Test
    void getMyProfile_사용자가_없으면_NotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getMyProfile(1L))
                .isInstanceOf(NotFoundException.class);
    }
}
