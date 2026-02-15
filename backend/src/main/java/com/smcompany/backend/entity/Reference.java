package com.smcompany.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 자료실 자료 엔티티.
 * <p>업로드된 파일(PDF, 문서 등)의 메타 정보와 다운로드 이력을 관리한다.</p>
 */
@Entity
@Table(name = "reference")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reference {

    /** 자료 고유 식별자 (PK, Auto Increment) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 소속 카테고리 (FK → reference_category.id) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ReferenceCategory category;

    /** 자료 제목 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 자료 설명 */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** 업로드된 원본 파일명 */
    @Column(name = "file_name", nullable = false)
    private String fileName;

    /** 서버 내 파일 저장 경로 */
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    /** 썸네일 이미지 저장 경로 */
    @Column(name = "thumbnail_path", length = 500)
    private String thumbnailPath;

    /** 다운로드 횟수 (기본값 0) */
    @Column(name = "download_count")
    private Integer downloadCount = 0;

    /** 자료 등록 일시 (최초 저장 시 자동 설정) */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 자료 수정 일시 (수정 시 자동 갱신) */
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
    public Reference(ReferenceCategory category, String title, String description,
                     String fileName, String filePath, String thumbnailPath) {
        this.category = category;
        this.title = title;
        this.description = description;
        this.fileName = fileName;
        this.filePath = filePath;
        this.thumbnailPath = thumbnailPath;
        this.downloadCount = 0;
    }

    public void incrementDownloadCount() {
        this.downloadCount++;
    }

    public void update(String title, String description, ReferenceCategory category) {
        this.title = title;
        this.description = description;
        this.category = category;
    }
}
