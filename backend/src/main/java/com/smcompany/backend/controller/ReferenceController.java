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
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail) {

        ReferenceRequest request = new ReferenceRequest();
        request.setCategoryId(categoryId);
        request.setTitle(title);
        request.setDescription(description);

        return ResponseEntity.ok(referenceService.createReference(request, file, thumbnail));
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
