package com.smcompany.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Q&A 게시글 엔티티.
 * <p>비회원도 작성 가능한 질의응답 게시판의 게시글 정보를 관리한다.
 * 게시글 수정/삭제 시 비밀번호 검증을 통해 작성자를 확인한다.</p>
 */
@Entity
@Table(name = "qna_post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaPost {

    /** 게시글 고유 식별자 (PK, Auto Increment) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 작성자 이름 */
    @Column(name = "author_name", nullable = false, length = 50)
    private String authorName;

    /** 게시글 비밀번호 (수정/삭제 시 본인 확인용) */
    @Column(nullable = false)
    private String password;

    /** 게시글 제목 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 게시글 본문 내용 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 조회수 (기본값 0) */
    @Column(name = "view_count")
    private Integer viewCount = 0;

    /** 게시글 작성 일시 (최초 저장 시 자동 설정) */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 게시글 수정 일시 (수정 시 자동 갱신) */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 게시글에 달린 댓글 목록 (게시글 삭제 시 댓글도 함께 삭제) */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QnaComment> comments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public QnaPost(String authorName, String password, String title, String content) {
        this.authorName = authorName;
        this.password = password;
        this.title = title;
        this.content = content;
        this.viewCount = 0;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
