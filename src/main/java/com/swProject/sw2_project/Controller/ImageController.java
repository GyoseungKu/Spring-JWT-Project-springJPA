package com.swProject.sw2_project.Controller;

import com.swProject.sw2_project.Service.ImageService;
import com.swProject.sw2_project.Entity.CmmnUser;
import com.swProject.sw2_project.Entity.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CmmnUser user
    ) {
        try {
            Image image = imageService.uploadProfileImage(file, user);
            String imageUrl = "https://cdn.gyoseung.me/sw2/" + image.getSavedFilename();
            return ResponseEntity.ok().body("업로드 성공! " + imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("업로드 실패: " + e.getMessage());
        }
    }
}
