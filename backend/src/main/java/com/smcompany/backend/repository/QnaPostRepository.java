package com.smcompany.backend.repository;

import com.smcompany.backend.entity.QnaPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaPostRepository extends JpaRepository<QnaPost, Long> {
    Page<QnaPost> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
