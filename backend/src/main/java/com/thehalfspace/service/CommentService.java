package com.thehalfspace.service;

import com.thehalfspace.dto.CommentRequest;
import com.thehalfspace.dto.CommentResponse;
import com.thehalfspace.dto.MatchDiscussionResponse;
import com.thehalfspace.entity.Comment;
import com.thehalfspace.entity.Match;
import com.thehalfspace.entity.Post;
import com.thehalfspace.entity.User;
import com.thehalfspace.exception.BusinessException;
import com.thehalfspace.exception.ErrorCode;
import com.thehalfspace.exception.NotFoundException;
import com.thehalfspace.repository.CommentRepository;
import com.thehalfspace.repository.MatchRepository;
import com.thehalfspace.repository.PostRepository;
import com.thehalfspace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public Page<CommentResponse> getMatchComments(Long matchId, Pageable pageable) {
        Page<Comment> roots = commentRepository.findByMatchIdAndParentIsNullOrderByCreatedAtAsc(matchId, pageable);

        List<Long> rootIds = roots.getContent().stream().map(Comment::getId).toList();
        Map<Long, List<CommentResponse>> repliesByParent = commentRepository
                .findByParentIdInOrderByCreatedAtAsc(rootIds)
                .stream()
                .map(CommentResponse::from)
                .collect(Collectors.groupingBy(CommentResponse::parentId));

        return roots.map(root -> CommentResponse.from(
                root, repliesByParent.getOrDefault(root.getId(), List.of())
        ));
    }

    @Transactional
    public CommentResponse createMatchComment(Long matchId, Long userId, CommentRequest request) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MATCH_NOT_FOUND));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));

        Comment parent = resolveParent(request.parentId());

        Comment comment = Comment.ofMatch(match, author, parent, request.content());
        return CommentResponse.from(commentRepository.save(comment));
    }

    public Page<CommentResponse> getPostComments(Long postId, Pageable pageable) {
        Page<Comment> roots = commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtAsc(postId, pageable);

        List<Long> rootIds = roots.getContent().stream().map(Comment::getId).toList();
        Map<Long, List<CommentResponse>> repliesByParent = commentRepository
                .findByParentIdInOrderByCreatedAtAsc(rootIds)
                .stream()
                .map(CommentResponse::from)
                .collect(Collectors.groupingBy(CommentResponse::parentId));

        return roots.map(root -> CommentResponse.from(
                root, repliesByParent.getOrDefault(root.getId(), List.of())
        ));
    }

    @Transactional
    public CommentResponse createPostComment(Long postId, Long userId, CommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));

        Comment parent = resolveParent(request.parentId());

        Comment comment = Comment.ofPost(post, author, parent, request.content());
        return CommentResponse.from(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.isAuthor(userId)) {
            throw new BusinessException(ErrorCode.COMMENT_FORBIDDEN);
        }

        comment.softDelete();
    }

    /**
     * "오늘의 토론장" — UTC 기준 오늘 경기 목록에 댓글 수를 붙여 반환한다.
     * 프론트에서 홈 화면에 경기일을 부각시키는 데 사용한다.
     */
    public List<MatchDiscussionResponse> getTodayDiscussions() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        Instant from = today.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant to = today.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        List<Match> matches = matchRepository.findByUtcDateBetweenOrderByUtcDate(from, to);
        List<Long> matchIds = matches.stream().map(Match::getId).toList();

        Map<Long, Long> countsByMatchId = matchIds.isEmpty()
                ? Map.of()
                : commentRepository.countByMatchIdIn(matchIds).stream()
                        .collect(Collectors.toMap(
                                CommentRepository.MatchCommentCount::getMatchId,
                                CommentRepository.MatchCommentCount::getCnt
                        ));

        return matches.stream()
                .map(match -> MatchDiscussionResponse.of(match, countsByMatchId.getOrDefault(match.getId(), 0L)))
                .toList();
    }

    private Comment resolveParent(Long parentId) {
        if (parentId == null) {
            return null;
        }
        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
        if (parent.getParent() != null) {
            throw new BusinessException(ErrorCode.INVALID_PARENT_COMMENT);
        }
        return parent;
    }
}
