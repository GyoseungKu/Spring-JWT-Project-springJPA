package com.swProject.sw2_project.Repository;

import com.swProject.sw2_project.Entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByCategoryId(Long categoryId);
}
