package com.swProject.sw2_project.Controller;

import com.swProject.sw2_project.DTO.ContentSummaryDTO;
import com.swProject.sw2_project.Service.ContentBookmarkService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/contents")
public class ContentBookmarkController {

    private final ContentBookmarkService service;

    public ContentBookmarkController(ContentBookmarkService service) {
        this.service = service;
    }

    // (로그인) 컨텐츠 북마크
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{contentId}/bookmarks")
    public ResponseEntity<Void> add(@PathVariable Long contentId, Principal principal) {
        String userId = principal != null ? principal.getName() : null;
        service.addBookmark(userId, contentId);
        return ResponseEntity.status(201).build();
    }

    // (로그인) 컨텐츠 북마크 취소
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{contentId}/bookmarks")
    public ResponseEntity<Void> remove(@PathVariable Long contentId, Principal principal) {
        String userId = principal != null ? principal.getName() : null;
        service.removeBookmark(userId, contentId);
        return ResponseEntity.noContent().build();
    }

    // (로그인) 내가 북마크한 컨텐츠 목록
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my-bookmarks")
    public ResponseEntity<List<ContentSummaryDTO>> myBookmarks(Principal principal) {
        String userId = principal != null ? principal.getName() : null;
        List<ContentSummaryDTO> list = service.listMyBookmarks(userId);
        return ResponseEntity.ok(list);
    }

    // (비로그인 허용) 특정 컨텐츠의 북마크 수
    @GetMapping("/{contentId}/bookmarks/count")
    public ResponseEntity<Long> count(@PathVariable Long contentId) {
        return ResponseEntity.ok(service.countByContent(contentId));
    }
}
