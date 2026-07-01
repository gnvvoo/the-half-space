package com.thehalfspace.repository;

import com.thehalfspace.entity.AiContent;
import com.thehalfspace.entity.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AiContentRepository extends JpaRepository<AiContent, UUID> {

    Optional<AiContent> findByMatchIdAndType(Long matchId, ContentType type);

    Optional<AiContent> findByMatchIdAndTypeAndIsPublished(Long matchId, ContentType type, boolean isPublished);

    boolean existsByMatchIdAndType(Long matchId, ContentType type);
}
