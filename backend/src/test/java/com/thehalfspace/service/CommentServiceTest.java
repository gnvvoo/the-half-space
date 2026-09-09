package com.thehalfspace.service;

import com.thehalfspace.dto.CommentRequest;
import com.thehalfspace.dto.CommentResponse;
import com.thehalfspace.dto.MatchDiscussionResponse;
import com.thehalfspace.entity.Board;
import com.thehalfspace.entity.Comment;
import com.thehalfspace.entity.Match;
import com.thehalfspace.entity.MatchStatus;
import com.thehalfspace.entity.Post;
import com.thehalfspace.entity.Team;
import com.thehalfspace.entity.User;
import com.thehalfspace.exception.BusinessException;
import com.thehalfspace.exception.NotFoundException;
import com.thehalfspace.repository.CommentRepository;
import com.thehalfspace.repository.MatchRepository;
import com.thehalfspace.repository.PostRepository;
import com.thehalfspace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private CommentRepository commentRepository;
    private MatchRepository matchRepository;
    private UserRepository userRepository;
    private PostRepository postRepository;

    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        matchRepository = mock(MatchRepository.class);
        userRepository = mock(UserRepository.class);
        postRepository = mock(PostRepository.class);

        commentService = new CommentService(commentRepository, matchRepository, userRepository, postRepository);
    }

    private User userWithId(Long id) {
        User user = User.ofLocal("user" + id + "@test.com", "user" + id, "hash");
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    @Test
    void createMatchComment_정상_생성() {
        Match match = Match.builder().id(1L).build();
        User author = userWithId(10L);

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(userRepository.findById(10L)).thenReturn(Optional.of(author));
        when(commentRepository.save(any())).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            ReflectionTestUtils.setField(c, "id", 100L);
            return c;
        });

        CommentResponse response = commentService.createMatchComment(1L, 10L, new CommentRequest("좋은 경기였다", null));

        assertThat(response.matchId()).isEqualTo(1L);
        assertThat(response.content()).isEqualTo("좋은 경기였다");
        assertThat(response.authorId()).isEqualTo(10L);
    }

    @Test
    void createMatchComment_경기가_없으면_NotFoundException() {
        when(matchRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createMatchComment(1L, 10L, new CommentRequest("내용", null)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createMatchComment_대댓글에_답글이면_INVALID_PARENT_COMMENT_예외() {
        Match match = Match.builder().id(1L).build();
        User author = userWithId(10L);
        User grandParentAuthor = userWithId(11L);
        Comment root = Comment.ofMatch(match, grandParentAuthor, null, "루트 댓글");
        ReflectionTestUtils.setField(root, "id", 5L);
        Comment reply = Comment.ofMatch(match, grandParentAuthor, root, "대댓글");
        ReflectionTestUtils.setField(reply, "id", 6L);

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(userRepository.findById(10L)).thenReturn(Optional.of(author));
        when(commentRepository.findById(6L)).thenReturn(Optional.of(reply));

        assertThatThrownBy(() -> commentService.createMatchComment(1L, 10L, new CommentRequest("대대댓글", 6L)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void deleteComment_작성자_본인이면_soft_delete() {
        Match match = Match.builder().id(1L).build();
        User author = userWithId(10L);
        Comment comment = Comment.ofMatch(match, author, null, "삭제될 댓글");
        ReflectionTestUtils.setField(comment, "id", 100L);

        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(100L, 10L);

        assertThat(comment.isDeleted()).isTrue();
    }

    @Test
    void deleteComment_작성자가_아니면_BusinessException() {
        Match match = Match.builder().id(1L).build();
        User author = userWithId(10L);
        Comment comment = Comment.ofMatch(match, author, null, "삭제될 댓글");
        ReflectionTestUtils.setField(comment, "id", 100L);

        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteComment(100L, 999L))
                .isInstanceOf(BusinessException.class);

        assertThat(comment.isDeleted()).isFalse();
    }

    @Test
    void deleteComment_댓글이_없으면_NotFoundException() {
        when(commentRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(100L, 10L))
                .isInstanceOf(NotFoundException.class);
    }

    private Post postWithId(Long id) {
        Board board = Board.of("epl", "프리미어리그", "설명");
        ReflectionTestUtils.setField(board, "id", 1L);
        User author = userWithId(1L);
        Post post = Post.of(board, author, "제목", "내용");
        ReflectionTestUtils.setField(post, "id", id);
        return post;
    }

    @Test
    void createPostComment_정상_생성() {
        Post post = postWithId(1L);
        User author = userWithId(10L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findById(10L)).thenReturn(Optional.of(author));
        when(commentRepository.save(any())).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            ReflectionTestUtils.setField(c, "id", 100L);
            return c;
        });

        CommentResponse response = commentService.createPostComment(1L, 10L, new CommentRequest("좋은 글이네요", null));

        assertThat(response.postId()).isEqualTo(1L);
        assertThat(response.content()).isEqualTo("좋은 글이네요");
        assertThat(response.authorId()).isEqualTo(10L);
    }

    @Test
    void createPostComment_게시글이_없으면_NotFoundException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createPostComment(1L, 10L, new CommentRequest("내용", null)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getPostComments_대댓글을_부모에_묶어서_반환한다() {
        Post post = postWithId(1L);
        User author = userWithId(10L);
        Comment root = Comment.ofPost(post, author, null, "루트 댓글");
        ReflectionTestUtils.setField(root, "id", 1L);
        Comment reply = Comment.ofPost(post, author, root, "답글");
        ReflectionTestUtils.setField(reply, "id", 2L);

        Pageable pageable = PageRequest.of(0, 20);
        when(commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtAsc(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(root), pageable, 1));
        when(commentRepository.findByParentIdInAndDeletedFalseOrderByCreatedAtAsc(List.of(1L)))
                .thenReturn(List.of(reply));

        Page<CommentResponse> result = commentService.getPostComments(1L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).replies()).hasSize(1);
        assertThat(result.getContent().get(0).replies().get(0).id()).isEqualTo(2L);
    }

    @Test
    void getMatchComments_대댓글을_부모에_묶어서_반환한다() {
        Match match = Match.builder().id(1L).build();
        User author = userWithId(10L);
        Comment root = Comment.ofMatch(match, author, null, "루트 댓글");
        ReflectionTestUtils.setField(root, "id", 1L);
        Comment reply = Comment.ofMatch(match, author, root, "답글");
        ReflectionTestUtils.setField(reply, "id", 2L);

        Pageable pageable = PageRequest.of(0, 20);
        when(commentRepository.findByMatchIdAndParentIsNullOrderByCreatedAtAsc(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(root), pageable, 1));
        when(commentRepository.findByParentIdInAndDeletedFalseOrderByCreatedAtAsc(List.of(1L)))
                .thenReturn(List.of(reply));

        Page<CommentResponse> result = commentService.getMatchComments(1L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).replies()).hasSize(1);
        assertThat(result.getContent().get(0).replies().get(0).id()).isEqualTo(2L);
    }

    /**
     * 삭제된 루트 댓글이 답글을 가진 경우, 리포지토리 쿼리(NOT EXISTS 기반)가 목록에 남겨두는
     * 케이스를 서비스 레이어에서 재현한다 — 실제 제외/포함 여부는 CommentRepository의 JPQL이
     * 담당하며(로컬에서는 Postgres가 필요해 contextLoads/CliRunnerTest와 동일하게 통합 테스트
     * 대상), 여기서는 서비스가 그 결과를 올바르게 placeholder로 변환해 전달하는지 검증한다.
     */
    @Test
    void getMatchComments_삭제된_루트도_답글이_있으면_placeholder로_유지된다() {
        Match match = Match.builder().id(1L).build();
        User author = userWithId(10L);
        Comment deletedRoot = Comment.ofMatch(match, author, null, "루트 댓글");
        ReflectionTestUtils.setField(deletedRoot, "id", 1L);
        deletedRoot.softDelete();
        Comment reply = Comment.ofMatch(match, author, deletedRoot, "답글");
        ReflectionTestUtils.setField(reply, "id", 2L);

        Pageable pageable = PageRequest.of(0, 20);
        when(commentRepository.findByMatchIdAndParentIsNullOrderByCreatedAtAsc(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(deletedRoot), pageable, 1));
        // 삭제된 답글은 리포지토리 쿼리(AndDeletedFalse)가 애초에 걸러내므로, 살아있는 답글만 전달된다.
        when(commentRepository.findByParentIdInAndDeletedFalseOrderByCreatedAtAsc(List.of(1L)))
                .thenReturn(List.of(reply));

        Page<CommentResponse> result = commentService.getMatchComments(1L, pageable);

        CommentResponse rootResponse = result.getContent().get(0);
        assertThat(rootResponse.deleted()).isTrue();
        assertThat(rootResponse.content()).isNull();
        assertThat(rootResponse.authorNickname()).isNull();
        assertThat(rootResponse.replies()).hasSize(1);
        assertThat(rootResponse.replies().get(0).id()).isEqualTo(2L);
    }

    @Test
    void getMatchComments_답글_조회는_삭제되지_않은_답글만_조회하는_리포지토리_메서드를_사용한다() {
        Match match = Match.builder().id(1L).build();
        User author = userWithId(10L);
        Comment root = Comment.ofMatch(match, author, null, "루트 댓글");
        ReflectionTestUtils.setField(root, "id", 1L);

        Pageable pageable = PageRequest.of(0, 20);
        when(commentRepository.findByMatchIdAndParentIsNullOrderByCreatedAtAsc(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(root), pageable, 1));
        when(commentRepository.findByParentIdInAndDeletedFalseOrderByCreatedAtAsc(List.of(1L)))
                .thenReturn(List.of());

        commentService.getMatchComments(1L, pageable);

        verify(commentRepository).findByParentIdInAndDeletedFalseOrderByCreatedAtAsc(List.of(1L));
    }

    /**
     * "오늘의 토론장" commentCount는 삭제된 댓글을 제외해야 한다. 실제 제외는
     * CommentRepository.countByMatchIdIn의 JPQL(c.deleted = false)이 담당하므로,
     * 여기서는 서비스가 그 집계 결과를 MatchDiscussionResponse로 올바르게 전달하는지 검증한다.
     */
    @Test
    void getTodayDiscussions_댓글수는_삭제되지_않은_댓글만_집계한_결과를_그대로_사용한다() {
        Team homeTeam = Team.of(1L, "홈팀", "홈", "HOM", null, "PL");
        Team awayTeam = Team.of(2L, "원정팀", "원정", "AWY", null, "PL");
        Match match = Match.builder()
                .id(1L)
                .competitionId("PL")
                .season("2025-26")
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .status(MatchStatus.SCHEDULED)
                .utcDate(Instant.now())
                .build();

        when(matchRepository.findByUtcDateBetweenOrderByUtcDate(any(), any())).thenReturn(List.of(match));
        CommentRepository.MatchCommentCount count = mock(CommentRepository.MatchCommentCount.class);
        when(count.getMatchId()).thenReturn(1L);
        when(count.getCnt()).thenReturn(3L);
        when(commentRepository.countByMatchIdIn(List.of(1L))).thenReturn(List.of(count));

        List<MatchDiscussionResponse> result = commentService.getTodayDiscussions();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).commentCount()).isEqualTo(3L);
    }
}
