package com.smcompany.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QnaCommentRequest {

    @NotBlank(message = "작성자명을 입력해주세요.")
    private String authorName;

    private String password;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    private Long parentId;
}
