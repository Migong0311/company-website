package com.smcompany.backend.dto.response;

import com.smcompany.backend.entity.QnaPost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class QnaPostResponse {
    private Long id;
    private String authorName;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static QnaPostResponse from(QnaPost post) {
        return QnaPostResponse.builder()
                .id(post.getId())
                .authorName(post.getAuthorName())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public static QnaPostResponse listFrom(QnaPost post) {
        return QnaPostResponse.builder()
                .id(post.getId())
                .authorName(post.getAuthorName())
                .title(post.getTitle())
                .viewCount(post.getViewCount())
                .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                .createdAt(post.getCreatedAt())
                .build();
    }
}
