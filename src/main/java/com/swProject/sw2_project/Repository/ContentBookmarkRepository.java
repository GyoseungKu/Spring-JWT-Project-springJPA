package com.swProject.sw2_project.Repository;

import com.swProject.sw2_project.Entity.ContentBookmark;
import com.swProject.sw2_project.Entity.ContentBookmarkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContentBookmarkRepository extends JpaRepository<ContentBookmark, ContentBookmarkId> {

    boolean existsByIdUserIdAndIdContentId(String userId, Long contentId);

    void deleteByIdUserIdAndIdContentId(String userId, Long contentId);

    long countByIdContentId(Long contentId);

    @Query("select cb from ContentBookmark cb join fetch cb.content where cb.id.userId = :userId")
    List<ContentBookmark> findAllWithContentByUserId(String userId);
}
