package com.thehalfspace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.time.Instant;

/**
 * 경기(match) 토론 댓글 및 (Phase 4 예정) 게시글(post) 댓글을 함께 담는 테이블.
 *
 * matchId / postId 중 정확히 하나만 non-null이어야 한다 — 다형 연관관계(@Any) 대신
 * 두 nullable FK 컬럼 + CHECK 제약으로 FK 무결성과 집계 쿼리(경기별 댓글 수 등)를 보존한다.
 * postId는 Post 엔티티가 아직 없어 FK를 걸지 않은 순수 컬럼이며, Phase 4에서 Post 도입 시
 * 스키마 변경 없이 FK만 추가하면 된다.
 *
 * Flyway 베이스라인(V1__baseline.sql) 생성 시 다음 CHECK 제약을 반드시 포함할 것:
 * ALTER TABLE comments ADD CONSTRAINT chk_comments_target
 *   CHECK ((match_id IS NOT NULL) <> (post_id IS NOT NULL));
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "comments", indexes = {
        @Index(name = "idx_comments_match_id", columnList = "match_id"),
        @Index(name = "idx_comments_post_id", columnList = "post_id"),
        @Index(name = "idx_comments_parent_id", columnList = "parent_id")
})
@Check(constraints = "(match_id IS NOT NULL) <> (post_id IS NOT NULL)")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    // Post 엔티티가 아직 없어 FK 없이 순수 컬럼으로 둔다 (Phase 4에서 FK 추가 예정).
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // 대댓글은 1단계까지만 허용 — parent도 대댓글인 경우는 서비스 레이어에서 검증한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    private Comment(Match match, Long postId, User author, Comment parent, String content) {
        this.match = match;
        this.postId = postId;
        this.author = author;
        this.parent = parent;
        this.content = content;
    }

    public static Comment ofMatch(Match match, User author, Comment parent, String content) {
        return new Comment(match, null, author, parent, content);
    }

    public static Comment ofPost(Long postId, User author, Comment parent, String content) {
        return new Comment(null, postId, author, parent, content);
    }

    public void softDelete() {
        this.deleted = true;
        this.updatedAt = Instant.now();
    }

    public boolean isAuthor(Long userId) {
        return this.author.getId().equals(userId);
    }
}
