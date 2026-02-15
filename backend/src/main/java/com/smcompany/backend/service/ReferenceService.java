package com.smcompany.backend.service;

import com.smcompany.backend.dto.request.ReferenceRequest;
import com.smcompany.backend.dto.response.ReferenceResponse;
import com.smcompany.backend.entity.Reference;
import com.smcompany.backend.entity.ReferenceCategory;
import com.smcompany.backend.entity.ReferenceFile;
import com.smcompany.backend.entity.ReferenceImage;
import com.smcompany.backend.repository.ReferenceCategoryRepository;
import com.smcompany.backend.repository.ReferenceFileRepository;
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
    private final ReferenceFileRepository referenceFileRepository;
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
    public ReferenceResponse createReference(ReferenceRequest request, List<MultipartFile> files,
                                              MultipartFile thumbnail, List<MultipartFile> images) {
        ReferenceCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));

        // 첫 번째 파일을 메인 파일로 저장
        MultipartFile mainFile = files.get(0);
        String storedFileName = fileStorageService.storeFile(mainFile);
        String thumbnailName = null;
        if (thumbnail != null && !thumbnail.isEmpty()) {
            thumbnailName = fileStorageService.storeThumbnail(thumbnail);
        }

        Reference reference = Reference.builder()
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .fileName(mainFile.getOriginalFilename())
                .filePath(storedFileName)
                .thumbnailPath(thumbnailName)
                .build();

        Reference saved = referenceRepository.save(reference);

        // 추가 파일들을 ReferenceFile로 저장
        for (int i = 1; i < files.size(); i++) {
            MultipartFile f = files.get(i);
            String stored = fileStorageService.storeFile(f);
            ReferenceFile refFile = ReferenceFile.builder()
                    .reference(saved)
                    .fileName(f.getOriginalFilename())
                    .filePath(stored)
                    .sortOrder(i)
                    .build();
            referenceFileRepository.save(refFile);
        }

        // 갤러리 이미지 저장
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

    @Transactional
    public Resource downloadAttachedFile(Long fileId) {
        ReferenceFile refFile = referenceFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
        refFile.getReference().incrementDownloadCount();
        return fileStorageService.loadFileAsResource(refFile.getFilePath());
    }

    public String getAttachedFileName(Long fileId) {
        ReferenceFile refFile = referenceFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
        return refFile.getFileName();
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
