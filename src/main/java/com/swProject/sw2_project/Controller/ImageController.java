package com.swProject.sw2_project.Controller;

import com.swProject.sw2_project.Service.ImageService;
import com.swProject.sw2_project.Entity.Image;
import com.swProject.sw2_project.Repository.ImageRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;
    private final ImageRepository imageRepository; // 추가

    public ImageController(ImageService imageService, ImageRepository imageRepository) {
        this.imageService = imageService;
        this.imageRepository = imageRepository;
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            java.security.Principal principal
    ) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(401).body("인증 정보가 없습니다.");
        }
        try {
            String userId = principal.getName();
            Image image = imageService.uploadProfileImage(file, userId);
            String imageUrl = "https://cdn.gyoseung.me/sw2/" + image.getSavedFilename();
            return ResponseEntity.ok().body("업로드 성공! " + imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("업로드 실패: " + e.getMessage());
        }
    }

    @GetMapping("/{imageId}")
    public Map<String, Object> getImageUrl(@PathVariable Long imageId) {
        return imageRepository.findById(imageId)
                .map(image -> {
                    String url = "https://cdn.gyoseung.me/sw2/" + image.getSavedFilename();
                    Map<String, Object> result = new java.util.HashMap<>();
                    result.put("imageId", image.getImageId());
                    result.put("url", url);
                    return result;
                })
                .orElseThrow(() -> new RuntimeException("이미지를 찾을 수 없습니다."));
    }
}
