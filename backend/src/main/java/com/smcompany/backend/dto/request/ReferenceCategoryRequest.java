package com.smcompany.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReferenceCategoryRequest {

    @NotBlank(message = "카테고리명을 입력해주세요.")
    private String name;

    private Integer sortOrder;
}
