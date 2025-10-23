package com.swProject.sw2_project.Service;

import com.swProject.sw2_project.Entity.CmmnUser;
import com.swProject.sw2_project.Entity.Content;
import com.swProject.sw2_project.Entity.ContentBookmark;
import com.swProject.sw2_project.Entity.ContentBookmarkId;
import com.swProject.sw2_project.Repository.CmmnUserRepository;
import com.swProject.sw2_project.Repository.ContentBookmarkRepository;
import com.swProject.sw2_project.Repository.ContentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ContentBookmarkService {

    private final ContentBookmarkRepository bookmarkRepo;
    private final ContentRepository contentRepo;
    private final CmmnUserRepository userRepo;

    public ContentBookmarkService(ContentBookmarkRepository bookmarkRepo,
                                  ContentRepository contentRepo,
                                  CmmnUserRepository userRepo) {
        this.bookmarkRepo = bookmarkRepo;
        this.contentRepo = contentRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public void addBookmark(String userId, Long contentId) {
        ensureLoggedIn(userId);
        Content content = contentRepo.findById(contentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "content not found"));

        if (bookmarkRepo.existsByIdUserIdAndIdContentId(userId, contentId)) return;

        CmmnUser userRef = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        ContentBookmark cb = new ContentBookmark();
        cb.setId(new ContentBookmarkId(userId, contentId));
        cb.setUser(userRef);
        cb.setContent(content);
        bookmarkRepo.save(cb);
    }

    @Transactional
    public void removeBookmark(String userId, Long contentId) {
        ensureLoggedIn(userId);
        bookmarkRepo.deleteByIdUserIdAndIdContentId(userId, contentId);
    }

    @Transactional(readOnly = true)
    public List<com.swProject.sw2_project.DTO.ContentSummaryDTO> listMyBookmarks(String userId) {
        ensureLoggedIn(userId);
        return bookmarkRepo.findAllWithContentByUserId(userId)
                .stream()
                .map(cb -> com.swProject.sw2_project.DTO.ContentSummaryDTO.from(cb.getContent()))
                .toList();
    }

    @Transactional(readOnly = true)
    public long countByContent(Long contentId) {
        // 존재하지 않는 컨텐츠의 경우 404 응답
        if (!contentRepo.existsById(contentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "content not found");
        }
        return bookmarkRepo.countByIdContentId(contentId);
    }

    private void ensureLoggedIn(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "login required");
        }
    }
}
