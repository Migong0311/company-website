package com.smcompany.backend.dto.response;

import com.smcompany.backend.entity.Reference;
import com.smcompany.backend.entity.ReferenceFile;
import com.smcompany.backend.entity.ReferenceImage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<FileInfo> files;
    private List<ImageInfo> images;

    @Getter
    @Builder
    public static class FileInfo {
        private Long id;
        private String fileName;
        private Integer sortOrder;
    }

    @Getter
    @Builder
    public static class ImageInfo {
        private Long id;
        private String fileName;
        private Integer sortOrder;
    }

    public static ReferenceResponse from(Reference reference) {
        List<FileInfo> fileInfos = reference.getFiles() != null
                ? reference.getFiles().stream()
                    .map(f -> FileInfo.builder()
                            .id(f.getId())
                            .fileName(f.getFileName())
                            .sortOrder(f.getSortOrder())
                            .build())
                    .toList()
                : List.of();

        List<ImageInfo> imageInfos = reference.getImages() != null
                ? reference.getImages().stream()
                    .map(img -> ImageInfo.builder()
                            .id(img.getId())
                            .fileName(img.getFileName())
                            .sortOrder(img.getSortOrder())
                            .build())
                    .toList()
                : List.of();

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
                .files(fileInfos)
                .images(imageInfos)
                .build();
    }
}
