package com.swProject.sw2_project.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "content_bookmarks")
@Data
public class ContentBookmark {

    @EmbeddedId
    private ContentBookmarkId id = new ContentBookmarkId();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private CmmnUser user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("contentId")
    @JoinColumn(name = "content_id", referencedColumnName = "id", nullable = false)
    private Content content;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }
}
