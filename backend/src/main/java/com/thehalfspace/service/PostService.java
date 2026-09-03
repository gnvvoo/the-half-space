package com.thehalfspace.service;

import com.thehalfspace.dto.PostRequest;
import com.thehalfspace.dto.PostResponse;
import com.thehalfspace.entity.Board;
import com.thehalfspace.entity.Post;
import com.thehalfspace.entity.User;
import com.thehalfspace.exception.BusinessException;
import com.thehalfspace.exception.ErrorCode;
import com.thehalfspace.exception.NotFoundException;
import com.thehalfspace.repository.BoardRepository;
import com.thehalfspace.repository.PostRepository;
import com.thehalfspace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public Page<PostResponse> getPosts(Long boardId, String title, Pageable pageable) {
        Page<Post> posts = StringUtils.hasText(title)
                ? postRepository.findByBoardIdAndDeletedFalseAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(boardId, title, pageable)
                : postRepository.findByBoardIdAndDeletedFalseOrderByCreatedAtDesc(boardId, pageable);
        return posts.map(PostResponse::from);
    }

    @Transactional
    public PostResponse getPost(Long postId) {
        Post post = findActivePost(postId);
        post.increaseViewCount();
        return PostResponse.from(post);
    }

    @Transactional
    public PostResponse createPost(Long boardId, Long userId, PostRequest request) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.BOARD_NOT_FOUND));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));

        Post post = Post.of(board, author, request.title(), request.content());
        return PostResponse.from(postRepository.save(post));
    }

    @Transactional
    public PostResponse updatePost(Long postId, Long userId, PostRequest request) {
        Post post = findActivePost(postId);

        if (!post.isAuthor(userId)) {
            throw new BusinessException(ErrorCode.POST_FORBIDDEN);
        }

        post.update(request.title(), request.content());
        return PostResponse.from(post);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = findActivePost(postId);

        if (!post.isAuthor(userId)) {
            throw new BusinessException(ErrorCode.POST_FORBIDDEN);
        }

        post.softDelete();
    }

    private Post findActivePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));
        if (post.isDeleted()) {
            throw new NotFoundException(ErrorCode.POST_NOT_FOUND);
        }
        return post;
    }
}
