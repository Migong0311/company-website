package com.smcompany.backend.dto.response;

import com.smcompany.backend.entity.Reference;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReferenceResponse {
    private Long id;
    private String categoryName;
    private Long categoryId;
    private String title;
    private String description;
    private String fileName;
    private String thumbnailPath;
    private Integer downloadCount;
    private LocalDateTime createdAt;

    public static ReferenceResponse from(Reference reference) {
        return ReferenceResponse.builder()
                .id(reference.getId())
                .categoryName(reference.getCategory() != null ? reference.getCategory().getName() : null)
                .categoryId(reference.getCategory() != null ? reference.getCategory().getId() : null)
                .title(reference.getTitle())
                .description(reference.getDescription())
                .fileName(reference.getFileName())
                .thumbnailPath(reference.getThumbnailPath())
                .downloadCount(reference.getDownloadCount())
                .createdAt(reference.getCreatedAt())
                .build();
    }
}
