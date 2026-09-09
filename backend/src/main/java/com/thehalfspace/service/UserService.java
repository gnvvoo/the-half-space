package com.thehalfspace.service;

import com.thehalfspace.dto.UserResponse;
import com.thehalfspace.entity.User;
import com.thehalfspace.exception.ErrorCode;
import com.thehalfspace.exception.NotFoundException;
import com.thehalfspace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return UserResponse.from(user);
    }
}
