package com.swProject.sw2_project.Entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ContentBookmarkId implements Serializable {
    private String userId;
    private Long contentId;
}
