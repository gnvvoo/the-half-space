package com.thehalfspace.service;

import com.thehalfspace.dto.BoardResponse;
import com.thehalfspace.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    public List<BoardResponse> getBoards() {
        return boardRepository.findAllByOrderByIdAsc().stream()
                .map(BoardResponse::from)
                .toList();
    }
}
