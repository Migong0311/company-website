package com.smcompany.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "qna_comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private QnaPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private QnaComment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QnaComment> children = new ArrayList<>();

    @Column(name = "author_name", nullable = false, length = 50)
    private String authorName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_admin")
    private Boolean isAdmin = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

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
