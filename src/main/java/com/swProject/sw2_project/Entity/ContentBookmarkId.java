package com.swProject.sw2_project.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentBookmarkId implements Serializable {

    @Column(name = "user_id", length = 20, nullable = false)
    private String userId;

    @Column(name = "content_id", nullable = false)
    private Long contentId;
}
