package com.thehalfspace.service;

import com.thehalfspace.dto.PostRequest;
import com.thehalfspace.dto.PostResponse;
import com.thehalfspace.entity.Board;
import com.thehalfspace.entity.Post;
import com.thehalfspace.entity.User;
import com.thehalfspace.exception.BusinessException;
import com.thehalfspace.exception.NotFoundException;
import com.thehalfspace.repository.BoardRepository;
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

class PostServiceTest {

    private PostRepository postRepository;
    private BoardRepository boardRepository;
    private UserRepository userRepository;

    private PostService postService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        boardRepository = mock(BoardRepository.class);
        userRepository = mock(UserRepository.class);

        postService = new PostService(postRepository, boardRepository, userRepository);
    }

    private User userWithId(Long id) {
        User user = User.ofLocal("user" + id + "@test.com", "user" + id, "hash");
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Board boardWithId(Long id) {
        Board board = Board.of("epl", "프리미어리그", "설명");
        ReflectionTestUtils.setField(board, "id", id);
        return board;
    }

    @Test
    void createPost_정상_생성() {
        Board board = boardWithId(1L);
        User author = userWithId(10L);

        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(userRepository.findById(10L)).thenReturn(Optional.of(author));
        when(postRepository.save(any())).thenAnswer(inv -> {
            Post p = inv.getArgument(0);
            ReflectionTestUtils.setField(p, "id", 100L);
            return p;
        });

        PostResponse response = postService.createPost(1L, 10L, new PostRequest("제목", "내용"));

        assertThat(response.boardId()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("제목");
        assertThat(response.authorId()).isEqualTo(10L);
        assertThat(response.viewCount()).isEqualTo(0);
    }

    @Test
    void createPost_게시판이_없으면_NotFoundException() {
        when(boardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.createPost(1L, 10L, new PostRequest("제목", "내용")))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getPost_조회수가_증가한다() {
        Board board = boardWithId(1L);
        User author = userWithId(10L);
        Post post = Post.of(board, author, "제목", "내용");
        ReflectionTestUtils.setField(post, "id", 100L);

        when(postRepository.findById(100L)).thenReturn(Optional.of(post));

        PostResponse response = postService.getPost(100L);

        assertThat(response.viewCount()).isEqualTo(1);
        assertThat(post.getViewCount()).isEqualTo(1);
    }

    @Test
    void getPost_삭제된_게시글이면_NotFoundException() {
        Board board = boardWithId(1L);
        User author = userWithId(10L);
        Post post = Post.of(board, author, "제목", "내용");
        ReflectionTestUtils.setField(post, "id", 100L);
        post.softDelete();

        when(postRepository.findById(100L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.getPost(100L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updatePost_작성자_본인이면_수정된다() {
        Board board = boardWithId(1L);
        User author = userWithId(10L);
        Post post = Post.of(board, author, "제목", "내용");
        ReflectionTestUtils.setField(post, "id", 100L);

        when(postRepository.findById(100L)).thenReturn(Optional.of(post));

        PostResponse response = postService.updatePost(100L, 10L, new PostRequest("새 제목", "새 내용"));

        assertThat(response.title()).isEqualTo("새 제목");
        assertThat(response.content()).isEqualTo("새 내용");
    }

    @Test
    void updatePost_작성자가_아니면_BusinessException() {
        Board board = boardWithId(1L);
        User author = userWithId(10L);
        Post post = Post.of(board, author, "제목", "내용");
        ReflectionTestUtils.setField(post, "id", 100L);

        when(postRepository.findById(100L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.updatePost(100L, 999L, new PostRequest("새 제목", "새 내용")))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void deletePost_작성자_본인이면_soft_delete() {
        Board board = boardWithId(1L);
        User author = userWithId(10L);
        Post post = Post.of(board, author, "제목", "내용");
        ReflectionTestUtils.setField(post, "id", 100L);

        when(postRepository.findById(100L)).thenReturn(Optional.of(post));

        postService.deletePost(100L, 10L);

        assertThat(post.isDeleted()).isTrue();
    }

    @Test
    void deletePost_작성자가_아니면_BusinessException() {
        Board board = boardWithId(1L);
        User author = userWithId(10L);
        Post post = Post.of(board, author, "제목", "내용");
        ReflectionTestUtils.setField(post, "id", 100L);

        when(postRepository.findById(100L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.deletePost(100L, 999L))
                .isInstanceOf(BusinessException.class);

        assertThat(post.isDeleted()).isFalse();
    }

    @Test
    void getPosts_게시판별_목록을_최신순으로_반환한다() {
        Board board = boardWithId(1L);
        User author = userWithId(10L);
        Post post = Post.of(board, author, "제목", "내용");
        ReflectionTestUtils.setField(post, "id", 100L);

        Pageable pageable = PageRequest.of(0, 20);
        when(postRepository.findByBoardIdAndDeletedFalseOrderByCreatedAtDesc(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(post), pageable, 1));

        Page<PostResponse> result = postService.getPosts(1L, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(100L);
    }

    @Test
    void getPosts_제목_검색어가_있으면_검색_쿼리를_사용한다() {
        Board board = boardWithId(1L);
        User author = userWithId(10L);
        Post post = Post.of(board, author, "이적 소식", "내용");
        ReflectionTestUtils.setField(post, "id", 100L);

        Pageable pageable = PageRequest.of(0, 20);
        when(postRepository.findByBoardIdAndDeletedFalseAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(1L, "이적", pageable))
                .thenReturn(new PageImpl<>(List.of(post), pageable, 1));

        Page<PostResponse> result = postService.getPosts(1L, "이적", pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(postRepository).findByBoardIdAndDeletedFalseAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(1L, "이적", pageable);
        verify(postRepository, never()).findByBoardIdAndDeletedFalseOrderByCreatedAtDesc(any(), any());
    }
}
