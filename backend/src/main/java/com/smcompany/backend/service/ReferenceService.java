package com.smcompany.backend.service;

import com.smcompany.backend.dto.request.ReferenceRequest;
import com.smcompany.backend.dto.response.ReferenceResponse;
import com.smcompany.backend.entity.Reference;
import com.smcompany.backend.entity.ReferenceCategory;
import com.smcompany.backend.repository.ReferenceCategoryRepository;
import com.smcompany.backend.repository.ReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReferenceService {

    private final ReferenceRepository referenceRepository;
    private final ReferenceCategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    public Page<ReferenceResponse> getAllReferences(Pageable pageable) {
        return referenceRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(ReferenceResponse::from);
    }

    public Page<ReferenceResponse> getReferencesByCategory(Long categoryId, Pageable pageable) {
        return referenceRepository.findByCategoryId(categoryId, pageable)
                .map(ReferenceResponse::from);
    }

    public ReferenceResponse getReference(Long id) {
        Reference reference = referenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("자료를 찾을 수 없습니다."));
        return ReferenceResponse.from(reference);
    }

    @Transactional
    public ReferenceResponse createReference(ReferenceRequest request, MultipartFile file, MultipartFile thumbnail) {
        ReferenceCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));

        String storedFileName = fileStorageService.storeFile(file);
        String thumbnailName = null;
        if (thumbnail != null && !thumbnail.isEmpty()) {
            thumbnailName = fileStorageService.storeThumbnail(thumbnail);
        }

        Reference reference = Reference.builder()
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .fileName(file.getOriginalFilename())
                .filePath(storedFileName)
                .thumbnailPath(thumbnailName)
                .build();

        return ReferenceResponse.from(referenceRepository.save(reference));
    }

    @Transactional
    public Resource downloadFile(Long id) {
        Reference reference = referenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("자료를 찾을 수 없습니다."));
        reference.incrementDownloadCount();
        return fileStorageService.loadFileAsResource(reference.getFilePath());
    }

    public String getOriginalFileName(Long id) {
        Reference reference = referenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("자료를 찾을 수 없습니다."));
        return reference.getFileName();
    }

    @Transactional
    public void deleteReference(Long id) {
        Reference reference = referenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("자료를 찾을 수 없습니다."));

        if (reference.getFilePath() != null) {
            fileStorageService.deleteFile(reference.getFilePath());
        }
        if (reference.getThumbnailPath() != null) {
            fileStorageService.deleteThumbnail(reference.getThumbnailPath());
        }

        referenceRepository.delete(reference);
    }
}
