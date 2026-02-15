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
@Table(name = "reference_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReferenceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

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
