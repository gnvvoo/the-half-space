package com.thehalfspace.service;

import com.thehalfspace.dto.CommentRequest;
import com.thehalfspace.dto.CommentResponse;
import com.thehalfspace.entity.Comment;
import com.thehalfspace.entity.Match;
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
        when(commentRepository.findByParentIdInOrderByCreatedAtAsc(List.of(1L)))
                .thenReturn(List.of(reply));

        Page<CommentResponse> result = commentService.getMatchComments(1L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).replies()).hasSize(1);
        assertThat(result.getContent().get(0).replies().get(0).id()).isEqualTo(2L);
    }
}
