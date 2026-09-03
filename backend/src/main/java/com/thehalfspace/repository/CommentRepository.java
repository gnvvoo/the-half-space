package com.thehalfspace.repository;

import com.thehalfspace.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByMatchIdAndParentIsNullOrderByCreatedAtAsc(Long matchId, Pageable pageable);

    Page<Comment> findByPostIdAndParentIsNullOrderByCreatedAtAsc(Long postId, Pageable pageable);

    List<Comment> findByParentIdInOrderByCreatedAtAsc(List<Long> parentIds);

    long countByMatchId(Long matchId);

    long countByPostId(Long postId);

    @Query("select c.match.id as matchId, count(c) as cnt from Comment c " +
            "where c.match.id in :matchIds group by c.match.id")
    List<MatchCommentCount> countByMatchIdIn(@Param("matchIds") List<Long> matchIds);

    interface MatchCommentCount {
        Long getMatchId();
        Long getCnt();
    }
}
