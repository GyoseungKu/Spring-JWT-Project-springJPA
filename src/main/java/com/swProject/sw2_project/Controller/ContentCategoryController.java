package com.swProject.sw2_project.Controller;

import com.swProject.sw2_project.Entity.ContentCategory;
import com.swProject.sw2_project.Repository.ContentCategoryRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/content-categories")
public class ContentCategoryController {
    private final ContentCategoryRepository categoryRepository;

    public ContentCategoryController(ContentCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<ContentCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
}
