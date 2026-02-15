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
 * 자료실 카테고리 엔티티.
 * <p>자료(Reference)를 분류하기 위한 카테고리 정보를 관리한다.</p>
 */
@Entity
@Table(name = "reference_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReferenceCategory {

    /** 카테고리 고유 식별자 (PK, Auto Increment) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 카테고리명 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 정렬 순서 (오름차순, 기본값 0) */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /** 카테고리 생성 일시 (최초 저장 시 자동 설정) */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 해당 카테고리에 속한 자료 목록 */
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Reference> references = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public ReferenceCategory(String name, Integer sortOrder) {
        this.name = name;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
    }

    public void update(String name, Integer sortOrder) {
        this.name = name;
        if (sortOrder != null) {
            this.sortOrder = sortOrder;
        }
    }
}
