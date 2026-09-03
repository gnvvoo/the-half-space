package com.thehalfspace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 커뮤니티 게시판. 7개 리그/자유/이적 게시판을 앱 기동 시 {@code BoardSeeder}가
 * 코드 기준으로 idempotent하게 시딩한다 (epl, laliga, bundesliga, seriea, ligue1, free, transfer).
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    private Board(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static Board of(String code, String name, String description) {
        return new Board(code, name, description);
    }
}
