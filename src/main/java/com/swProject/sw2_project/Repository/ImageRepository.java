package com.swProject.sw2_project.Repository;

import com.swProject.sw2_project.Entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    boolean existsBySavedFilename(String savedFilename);
}
