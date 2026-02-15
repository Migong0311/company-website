package com.smcompany.backend.service;

import com.smcompany.backend.dto.request.QnaCommentRequest;
import com.smcompany.backend.dto.request.QnaPostRequest;
import com.smcompany.backend.dto.response.QnaCommentResponse;
import com.smcompany.backend.dto.response.QnaPostResponse;
import com.smcompany.backend.entity.QnaComment;
import com.smcompany.backend.entity.QnaPost;
import com.smcompany.backend.repository.QnaCommentRepository;
import com.smcompany.backend.repository.QnaPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaService {

    private final QnaPostRepository postRepository;
    private final QnaCommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    // ===== 게시글 =====

    public Page<QnaPostResponse> getAllPosts(Pageable pageable) {
        return postRepository.findAllByOrderByIsNoticeDescCreatedAtDesc(pageable)
                .map(QnaPostResponse::listFrom);
    }

    public Page<QnaPostResponse> searchPosts(String keyword, Pageable pageable) {
        return postRepository.findByTitleContainingOrContentContainingOrderByIsNoticeDescCreatedAtDesc(
                keyword, keyword, pageable).map(QnaPostResponse::listFrom);
    }

    @Transactional
    public QnaPostResponse getPost(Long id) {
        QnaPost post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        post.incrementViewCount();
        return QnaPostResponse.from(post);
    }

    @Transactional
    public QnaPostResponse createPost(QnaPostRequest request) {
        String encodedPassword = (request.getPassword() != null && !request.getPassword().isBlank())
                ? passwordEncoder.encode(request.getPassword())
                : passwordEncoder.encode("admin");
        QnaPost post = QnaPost.builder()
                .authorName(request.getAuthorName())
                .password(encodedPassword)
                .title(request.getTitle())
                .content(request.getContent())
                .isNotice(request.getIsNotice())
                .build();
        return QnaPostResponse.from(postRepository.save(post));
    }

    @Transactional
    public QnaPostResponse updatePost(Long id, QnaPostRequest request) {
        QnaPost post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        post.update(request.getTitle(), request.getContent(), request.getIsNotice());
        return QnaPostResponse.from(post);
    }

    @Transactional
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public boolean checkPostPassword(Long id, String rawPassword) {
        QnaPost post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        return passwordEncoder.matches(rawPassword, post.getPassword());
    }

    // ===== 댓글 =====

    public List<QnaCommentResponse> getComments(Long postId) {
        return commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtAsc(postId).stream()
                .map(QnaCommentResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public QnaCommentResponse createComment(Long postId, QnaCommentRequest request, boolean isAdmin) {
        QnaPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        QnaComment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("부모 댓글을 찾을 수 없습니다."));
        }

        String encodedPassword = (request.getPassword() != null && !request.getPassword().isBlank())
                ? passwordEncoder.encode(request.getPassword())
                : passwordEncoder.encode("admin");
        QnaComment comment = QnaComment.builder()
                .post(post)
                .parent(parent)
                .authorName(request.getAuthorName())
                .password(encodedPassword)
                .content(request.getContent())
                .isAdmin(isAdmin)
                .build();

        return QnaCommentResponse.from(commentRepository.save(comment));
    }

    @Transactional
    public QnaCommentResponse updateComment(Long commentId, String content) {
        QnaComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        comment.update(content);
        return QnaCommentResponse.from(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public boolean checkCommentPassword(Long commentId, String rawPassword) {
        QnaComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        return passwordEncoder.matches(rawPassword, comment.getPassword());
    }
}
