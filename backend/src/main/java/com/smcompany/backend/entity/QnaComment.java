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
 * Q&A 댓글 엔티티.
 * <p>게시글에 달리는 댓글 및 대댓글 정보를 관리한다.
 * 셀프 참조(parent-children)를 통해 무한 깊이의 대댓글을 지원한다.</p>
 */
@Entity
@Table(name = "qna_comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaComment {

    /** 댓글 고유 식별자 (PK, Auto Increment) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 소속 게시글 (FK → qna_post.id) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private QnaPost post;

    /** 부모 댓글 (FK → qna_comment.id, 최상위 댓글이면 null) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private QnaComment parent;

    /** 자식 댓글(대댓글) 목록 (부모 댓글 삭제 시 함께 삭제) */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QnaComment> children = new ArrayList<>();

    /** 댓글 작성자 이름 */
    @Column(name = "author_name", nullable = false, length = 50)
    private String authorName;

    /** 댓글 비밀번호 (수정/삭제 시 본인 확인용) */
    @Column(nullable = false)
    private String password;

    /** 댓글 내용 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 관리자 작성 여부 (true: 관리자 답변, false: 일반 댓글) */
    @Column(name = "is_admin")
    private Boolean isAdmin = false;

    /** 댓글 작성 일시 (최초 저장 시 자동 설정) */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 댓글 수정 일시 (수정 시 자동 갱신) */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
    public QnaComment(QnaPost post, QnaComment parent, String authorName,
                      String password, String content, Boolean isAdmin) {
        this.post = post;
        this.parent = parent;
        this.authorName = authorName;
        this.password = password;
        this.content = content;
        this.isAdmin = isAdmin != null ? isAdmin : false;
    }

    public void update(String content) {
        this.content = content;
    }
}
