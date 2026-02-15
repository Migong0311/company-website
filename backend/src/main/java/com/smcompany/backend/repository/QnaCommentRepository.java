package com.smcompany.backend.repository;

import com.smcompany.backend.entity.QnaComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaCommentRepository extends JpaRepository<QnaComment, Long> {
    List<QnaComment> findByPostIdAndParentIsNullOrderByCreatedAtAsc(Long postId);
}
