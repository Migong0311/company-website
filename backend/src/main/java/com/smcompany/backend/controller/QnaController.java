package com.smcompany.backend.controller;

import com.smcompany.backend.dto.request.PasswordCheckRequest;
import com.smcompany.backend.dto.request.QnaCommentRequest;
import com.smcompany.backend.dto.request.QnaCommentUpdateRequest;
import com.smcompany.backend.dto.request.QnaPostRequest;
import com.smcompany.backend.dto.response.QnaCommentResponse;
import com.smcompany.backend.dto.response.QnaPostResponse;
import com.smcompany.backend.service.QnaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/qna")
@RequiredArgsConstructor
public class QnaController {

    private final QnaService qnaService;

    // ===== 게시글 =====

    @GetMapping
    public ResponseEntity<Page<QnaPostResponse>> getAllPosts(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(qnaService.getAllPosts(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<QnaPostResponse>> searchPosts(
            @RequestParam String keyword,
            @PageableDefault(size = 15) Pageable pageable) {
        return ResponseEntity.ok(qnaService.searchPosts(keyword, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QnaPostResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(qnaService.getPost(id));
    }

    @PostMapping
    public ResponseEntity<QnaPostResponse> createPost(@Valid @RequestBody QnaPostRequest request) {
        return ResponseEntity.ok(qnaService.createPost(request));
    }

    @PostMapping("/{id}/check-password")
    public ResponseEntity<?> checkPostPassword(
            @PathVariable Long id, @Valid @RequestBody PasswordCheckRequest request) {
        boolean matches = qnaService.checkPostPassword(id, request.getPassword());
        return ResponseEntity.ok(Map.of("valid", matches));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QnaPostResponse> updatePost(
            @PathVariable Long id, @Valid @RequestBody QnaPostRequest request,
            HttpSession session) {
        String admin = (String) session.getAttribute("admin");
        boolean isAdmin = admin != null;

        if (!isAdmin && !qnaService.checkPostPassword(id, request.getPassword())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(qnaService.updatePost(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id, @Valid @RequestBody PasswordCheckRequest request,
            HttpSession session) {
        String admin = (String) session.getAttribute("admin");
        boolean isAdmin = admin != null;

        if (!isAdmin && !qnaService.checkPostPassword(id, request.getPassword())) {
            return ResponseEntity.status(403).build();
        }
        qnaService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // ===== 댓글 =====

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<QnaCommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(qnaService.getComments(postId));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<QnaCommentResponse> createComment(
            @PathVariable Long postId, @Valid @RequestBody QnaCommentRequest request,
            HttpSession session) {
        String admin = (String) session.getAttribute("admin");
        boolean isAdmin = admin != null;
        return ResponseEntity.ok(qnaService.createComment(postId, request, isAdmin));
    }

    @PostMapping("/comments/{commentId}/check-password")
    public ResponseEntity<?> checkCommentPassword(
            @PathVariable Long commentId, @Valid @RequestBody PasswordCheckRequest request) {
        boolean matches = qnaService.checkCommentPassword(commentId, request.getPassword());
        return ResponseEntity.ok(Map.of("valid", matches));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<QnaCommentResponse> updateComment(
            @PathVariable Long commentId, @Valid @RequestBody QnaCommentUpdateRequest request,
            HttpSession session) {
        String admin = (String) session.getAttribute("admin");
        boolean isAdmin = admin != null;

        if (!isAdmin && !qnaService.checkCommentPassword(commentId, request.getPassword())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(qnaService.updateComment(commentId, request.getContent()));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId, @Valid @RequestBody PasswordCheckRequest request,
            HttpSession session) {
        String admin = (String) session.getAttribute("admin");
        boolean isAdmin = admin != null;

        if (!isAdmin && !qnaService.checkCommentPassword(commentId, request.getPassword())) {
            return ResponseEntity.status(403).build();
        }
        qnaService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
