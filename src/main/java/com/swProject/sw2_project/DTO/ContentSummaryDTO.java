package com.swProject.sw2_project.DTO;

import com.swProject.sw2_project.Entity.Content;

public record ContentSummaryDTO(
        Long id,
        String title,
        String subtitle,
        String icon,
        Long categoryId
) {
    public static com.swProject.sw2_project.DTO.ContentSummaryDTO from(Content c) {
        Long catId = (c.getCategory() != null) ? c.getCategory().getId() : null;
        return new com.swProject.sw2_project.DTO.ContentSummaryDTO(c.getId(), c.getTitle(), c.getSubtitle(), c.getIcon(), catId);
    }
}
