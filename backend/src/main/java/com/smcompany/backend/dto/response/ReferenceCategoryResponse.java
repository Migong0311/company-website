package com.smcompany.backend.dto.response;

import com.smcompany.backend.entity.ReferenceCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReferenceCategoryResponse {
    private Long id;
    private String name;
    private Integer sortOrder;

    public static ReferenceCategoryResponse from(ReferenceCategory category) {
        return ReferenceCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .sortOrder(category.getSortOrder())
                .build();
    }
}
