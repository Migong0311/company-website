package com.smcompany.backend.repository;

import com.smcompany.backend.entity.ReferenceFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferenceFileRepository extends JpaRepository<ReferenceFile, Long> {
    List<ReferenceFile> findByReferenceIdOrderBySortOrderAsc(Long referenceId);
}
