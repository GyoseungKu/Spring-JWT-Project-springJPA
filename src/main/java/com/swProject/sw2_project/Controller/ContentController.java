package com.swProject.sw2_project.Controller;

import com.swProject.sw2_project.Entity.Content;
import com.swProject.sw2_project.Repository.ContentRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/contents")
public class ContentController {
    private final ContentRepository contentRepository;

    public ContentController(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    @GetMapping
    public List<Content> getAllContents() {
        return contentRepository.findAll();
    }

    @GetMapping("/category/{categoryId}")
    public List<Content> getContentsByCategory(@PathVariable Long categoryId) {
        return contentRepository.findByCategoryId(categoryId);
    }

    @GetMapping("/{id}")
    public Content getContentById(@PathVariable Long id) {
        return contentRepository.findById(id).orElse(null);
    }
}
