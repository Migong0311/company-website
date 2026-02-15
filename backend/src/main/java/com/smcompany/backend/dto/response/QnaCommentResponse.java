package com.smcompany.backend.dto.response;

import com.smcompany.backend.entity.QnaComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class QnaCommentResponse {
    private Long id;
    private String authorName;
    private String content;
    private Boolean isAdmin;
    private LocalDateTime createdAt;
    private List<QnaCommentResponse> children;

    public static QnaCommentResponse from(QnaComment comment) {
        return QnaCommentResponse.builder()
                .id(comment.getId())
                .authorName(comment.getAuthorName())
                .content(comment.getContent())
                .isAdmin(comment.getIsAdmin())
                .createdAt(comment.getCreatedAt())
                .children(comment.getChildren() != null
                        ? comment.getChildren().stream()
                            .map(QnaCommentResponse::from)
                            .collect(Collectors.toList())
                        : List.of())
                .build();
    }
}
