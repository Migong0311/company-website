package com.smcompany.backend.service;

import com.smcompany.backend.dto.request.ReferenceCategoryRequest;
import com.smcompany.backend.dto.response.ReferenceCategoryResponse;
import com.smcompany.backend.entity.ReferenceCategory;
import com.smcompany.backend.repository.ReferenceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReferenceCategoryService {

    private final ReferenceCategoryRepository categoryRepository;

    public List<ReferenceCategoryResponse> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc().stream()
                .map(ReferenceCategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReferenceCategoryResponse createCategory(ReferenceCategoryRequest request) {
        ReferenceCategory category = ReferenceCategory.builder()
                .name(request.getName())
                .sortOrder(request.getSortOrder())
                .build();
        return ReferenceCategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public ReferenceCategoryResponse updateCategory(Long id, ReferenceCategoryRequest request) {
        ReferenceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));
        category.update(request.getName(), request.getSortOrder());
        return ReferenceCategoryResponse.from(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
