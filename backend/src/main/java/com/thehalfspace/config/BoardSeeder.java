package com.thehalfspace.config;

import com.thehalfspace.entity.Board;
import com.thehalfspace.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 커뮤니티 게시판 7종을 앱 기동 시 idempotent하게 시딩한다 (code 기준 존재 여부 확인 후 삽입).
 * dev는 Hibernate ddl-auto: update로 스키마를 관리하므로 별도 마이그레이션 없이 동작하지만,
 * Flyway 베이스라인(V1__baseline.sql) 작성 시 이 시딩 데이터도 INSERT문으로 함께 포함해야 한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BoardSeeder implements CommandLineRunner {

    private static final List<Board> BOARDS = List.of(
            Board.of("epl", "프리미어리그", "잉글랜드 프리미어리그 게시판"),
            Board.of("laliga", "라리가", "스페인 라리가 게시판"),
            Board.of("bundesliga", "분데스리가", "독일 분데스리가 게시판"),
            Board.of("seriea", "세리에 A", "이탈리아 세리에 A 게시판"),
            Board.of("ligue1", "리그 1", "프랑스 리그 1 게시판"),
            Board.of("free", "자유게시판", "자유 주제 게시판"),
            Board.of("transfer", "이적 게시판", "이적시장 루머·소식 게시판")
    );

    private final BoardRepository boardRepository;

    @Override
    public void run(String... args) {
        BOARDS.forEach(board -> {
            if (boardRepository.findByCode(board.getCode()).isEmpty()) {
                boardRepository.save(board);
                log.info("게시판 시딩: {} ({})", board.getName(), board.getCode());
            }
        });
    }
}
