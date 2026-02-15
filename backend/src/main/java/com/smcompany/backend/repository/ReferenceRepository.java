package com.smcompany.backend.repository;

import com.smcompany.backend.entity.Reference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferenceRepository extends JpaRepository<Reference, Long> {
    Page<Reference> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Reference> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Reference> findByTitleContainingOrDescriptionContainingOrderByCreatedAtDesc(
            String title, String description, Pageable pageable);
}
