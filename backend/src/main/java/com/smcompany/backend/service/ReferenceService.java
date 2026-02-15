package com.smcompany.backend.service;

import com.smcompany.backend.dto.request.ReferenceRequest;
import com.smcompany.backend.dto.response.ReferenceResponse;
import com.smcompany.backend.entity.Reference;
import com.smcompany.backend.entity.ReferenceCategory;
import com.smcompany.backend.entity.ReferenceFile;
import com.smcompany.backend.entity.ReferenceImage;
import com.smcompany.backend.repository.ReferenceCategoryRepository;
import com.smcompany.backend.repository.ReferenceImageRepository;
import com.smcompany.backend.repository.ReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReferenceService {

    private final ReferenceRepository referenceRepository;
    private final ReferenceCategoryRepository categoryRepository;
    private final ReferenceImageRepository referenceImageRepository;
    private final FileStorageService fileStorageService;

    public Page<ReferenceResponse> getAllReferences(Pageable pageable) {
        return referenceRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(ReferenceResponse::from);
    }

    public Page<ReferenceResponse> searchReferences(String keyword, Pageable pageable) {
        return referenceRepository.findByTitleContainingOrDescriptionContainingOrderByCreatedAtDesc(
                keyword, keyword, pageable).map(ReferenceResponse::from);
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
    public ReferenceResponse createReference(ReferenceRequest request, MultipartFile file,
                                              MultipartFile thumbnail, List<MultipartFile> images) {
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

        Reference saved = referenceRepository.save(reference);

        // 갤러리 이미지 저장 (최대 3장)
        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile img = images.get(i);
                if (img != null && !img.isEmpty()) {
                    String storedImg = fileStorageService.storeThumbnail(img);
                    ReferenceImage refImage = ReferenceImage.builder()
                            .reference(saved)
                            .fileName(img.getOriginalFilename())
                            .filePath(storedImg)
                            .sortOrder(i)
                            .build();
                    referenceImageRepository.save(refImage);
                }
            }
        }

        return ReferenceResponse.from(referenceRepository.findById(saved.getId()).orElseThrow());
    }

    @Transactional
    public Resource downloadFile(Long id) {
        Reference reference = referenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("자료를 찾을 수 없습니다."));
        reference.incrementDownloadCount();
        return fileStorageService.loadFileAsResource(reference.getFilePath());
    }

    public Resource loadThumbnail(String thumbnailPath) {
        return fileStorageService.loadThumbnailAsResource(thumbnailPath);
    }

    public Resource loadGalleryImage(Long imageId) {
        ReferenceImage refImage = referenceImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("이미지를 찾을 수 없습니다."));
        return fileStorageService.loadThumbnailAsResource(refImage.getFilePath());
    }

    public String getGalleryImageFileName(Long imageId) {
        ReferenceImage refImage = referenceImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("이미지를 찾을 수 없습니다."));
        return refImage.getFilePath();
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

        // 메인 파일 삭제
        if (reference.getFilePath() != null) {
            fileStorageService.deleteFile(reference.getFilePath());
        }
        if (reference.getThumbnailPath() != null) {
            fileStorageService.deleteThumbnail(reference.getThumbnailPath());
        }

        // 추가 파일들 삭제
        for (ReferenceFile refFile : reference.getFiles()) {
            fileStorageService.deleteFile(refFile.getFilePath());
        }

        // 갤러리 이미지 삭제
        for (ReferenceImage refImage : reference.getImages()) {
            fileStorageService.deleteThumbnail(refImage.getFilePath());
        }

        referenceRepository.delete(reference);
    }
}
