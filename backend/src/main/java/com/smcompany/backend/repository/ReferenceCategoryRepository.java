package com.smcompany.backend.repository;

import com.smcompany.backend.entity.ReferenceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferenceCategoryRepository extends JpaRepository<ReferenceCategory, Long> {
    List<ReferenceCategory> findAllByOrderBySortOrderAsc();
}
