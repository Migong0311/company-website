package com.smcompany.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reference_file")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReferenceFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id", nullable = false)
    private Reference reference;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Builder
    public ReferenceFile(Reference reference, String fileName, String filePath, Integer sortOrder) {
        this.reference = reference;
        this.fileName = fileName;
        this.filePath = filePath;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
    }
}
