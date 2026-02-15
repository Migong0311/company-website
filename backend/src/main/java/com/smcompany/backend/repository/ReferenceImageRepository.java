package com.smcompany.backend.repository;

import com.smcompany.backend.entity.ReferenceImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferenceImageRepository extends JpaRepository<ReferenceImage, Long> {
    List<ReferenceImage> findByReferenceIdOrderBySortOrderAsc(Long referenceId);
}
