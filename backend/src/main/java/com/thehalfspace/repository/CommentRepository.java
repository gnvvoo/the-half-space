package com.thehalfspace.repository;

import com.thehalfspace.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 삭제된 루트 댓글은 "삭제되지 않은 답글"이 하나라도 있을 때만 placeholder로 유지한다.
     * 답글이 전혀 없거나 답글마저 모두 삭제된 경우에는 목록에서 완전히 제외한다.
     */
    @Query(value = "select c from Comment c "
            + "where c.match.id = :matchId and c.parent is null "
            + "and (c.deleted = false or exists (select 1 from Comment r where r.parent = c and r.deleted = false)) "
            + "order by c.createdAt asc",
            countQuery = "select count(c) from Comment c "
                    + "where c.match.id = :matchId and c.parent is null "
                    + "and (c.deleted = false or exists (select 1 from Comment r where r.parent = c and r.deleted = false))")
    Page<Comment> findByMatchIdAndParentIsNullOrderByCreatedAtAsc(@Param("matchId") Long matchId, Pageable pageable);

    @Query(value = "select c from Comment c "
            + "where c.post.id = :postId and c.parent is null "
            + "and (c.deleted = false or exists (select 1 from Comment r where r.parent = c and r.deleted = false)) "
            + "order by c.createdAt asc",
            countQuery = "select count(c) from Comment c "
                    + "where c.post.id = :postId and c.parent is null "
                    + "and (c.deleted = false or exists (select 1 from Comment r where r.parent = c and r.deleted = false))")
    Page<Comment> findByPostIdAndParentIsNullOrderByCreatedAtAsc(@Param("postId") Long postId, Pageable pageable);

    // 대댓글은 1단계까지만 허용되어 자식이 없으므로, 삭제된 답글은 placeholder 없이 그냥 제외한다.
    List<Comment> findByParentIdInAndDeletedFalseOrderByCreatedAtAsc(List<Long> parentIds);

    long countByMatchIdAndDeletedFalse(Long matchId);

    long countByPostIdAndDeletedFalse(Long postId);

    @Query("select c.match.id as matchId, count(c) as cnt from Comment c " +
            "where c.match.id in :matchIds and c.deleted = false group by c.match.id")
    List<MatchCommentCount> countByMatchIdIn(@Param("matchIds") List<Long> matchIds);

    interface MatchCommentCount {
        Long getMatchId();
        Long getCnt();
    }
}
