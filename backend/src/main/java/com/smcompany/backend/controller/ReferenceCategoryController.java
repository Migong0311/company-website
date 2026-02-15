package com.smcompany.backend.controller;

import com.smcompany.backend.dto.request.ReferenceCategoryRequest;
import com.smcompany.backend.dto.response.ReferenceCategoryResponse;
import com.smcompany.backend.service.ReferenceCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reference-categories")
@RequiredArgsConstructor
public class ReferenceCategoryController {

    private final ReferenceCategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<ReferenceCategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<ReferenceCategoryResponse> createCategory(
            @Valid @RequestBody ReferenceCategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReferenceCategoryResponse> updateCategory(
            @PathVariable Long id, @Valid @RequestBody ReferenceCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
