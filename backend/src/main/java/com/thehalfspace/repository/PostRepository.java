package com.thehalfspace.repository;

import com.thehalfspace.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByBoardIdAndDeletedFalseOrderByCreatedAtDesc(Long boardId, Pageable pageable);

    Page<Post> findByBoardIdAndDeletedFalseAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
            Long boardId, String title, Pageable pageable);
}
