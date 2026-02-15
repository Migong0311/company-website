package com.smcompany.backend.controller;

import com.smcompany.backend.dto.request.ReferenceRequest;
import com.smcompany.backend.dto.response.ReferenceResponse;
import com.smcompany.backend.service.ReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/references")
@RequiredArgsConstructor
public class ReferenceController {

    private final ReferenceService referenceService;

    @GetMapping
    public ResponseEntity<Page<ReferenceResponse>> getAllReferences(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(referenceService.getAllReferences(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ReferenceResponse>> searchReferences(
            @RequestParam String keyword,
            @PageableDefault(size = 8) Pageable pageable) {
        return ResponseEntity.ok(referenceService.searchReferences(keyword, pageable));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ReferenceResponse>> getReferencesByCategory(
            @PathVariable Long categoryId, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(referenceService.getReferencesByCategory(categoryId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReferenceResponse> getReference(@PathVariable Long id) {
        return ResponseEntity.ok(referenceService.getReference(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReferenceResponse> createReference(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        if (images != null && images.size() > 5) {
            return ResponseEntity.badRequest().build();
        }

        ReferenceRequest request = new ReferenceRequest();
        request.setCategoryId(categoryId);
        request.setTitle(title);
        request.setDescription(description);

        return ResponseEntity.ok(referenceService.createReference(request, file, thumbnail, images));
    }

    @GetMapping("/{id}/thumbnail")
    public ResponseEntity<Resource> getThumbnail(@PathVariable Long id) {
        ReferenceResponse ref = referenceService.getReference(id);
        if (ref.getThumbnailPath() == null) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = referenceService.loadThumbnail(ref.getThumbnailPath());
        String ext = ref.getThumbnailPath().substring(ref.getThumbnailPath().lastIndexOf('.') + 1).toLowerCase();
        MediaType mediaType = switch (ext) {
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "webp" -> MediaType.parseMediaType("image/webp");
            default -> MediaType.IMAGE_JPEG;
        };
        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<Resource> getGalleryImage(@PathVariable Long imageId) {
        Resource resource = referenceService.loadGalleryImage(imageId);
        String filePath = referenceService.getGalleryImageFileName(imageId);
        String ext = filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();
        MediaType mediaType = switch (ext) {
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "webp" -> MediaType.parseMediaType("image/webp");
            default -> MediaType.IMAGE_JPEG;
        };
        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        Resource resource = referenceService.downloadFile(id);
        String fileName = referenceService.getOriginalFileName(id);
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedFileName)
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReference(@PathVariable Long id) {
        referenceService.deleteReference(id);
        return ResponseEntity.noContent().build();
    }
}
