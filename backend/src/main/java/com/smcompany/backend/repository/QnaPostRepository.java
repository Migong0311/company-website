package com.smcompany.backend.repository;

import com.smcompany.backend.entity.QnaPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaPostRepository extends JpaRepository<QnaPost, Long> {
    Page<QnaPost> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<QnaPost> findAllByOrderByIsNoticeDescCreatedAtDesc(Pageable pageable);
    Page<QnaPost> findByTitleContainingOrContentContainingOrderByIsNoticeDescCreatedAtDesc(
            String title, String content, Pageable pageable);
}
