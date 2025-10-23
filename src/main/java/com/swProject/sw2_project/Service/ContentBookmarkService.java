package com.swProject.sw2_project.Service;

import com.swProject.sw2_project.DTO.ContentSummaryDTO;
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

        if (bookmarkRepo.existsByIdUserIdAndIdContentId(userId, contentId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "already bookmarked");
        }

        CmmnUser user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        ContentBookmark cb = new ContentBookmark();
        cb.setId(new ContentBookmarkId(userId, contentId));
        cb.setUser(user);
        cb.setContent(content);
        bookmarkRepo.save(cb);
    }

    @Transactional
    public void removeBookmark(String userId, Long contentId) {
        ensureLoggedIn(userId);

        if (!bookmarkRepo.existsByIdUserIdAndIdContentId(userId, contentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not bookmarked");
        }
        bookmarkRepo.deleteByIdUserIdAndIdContentId(userId, contentId);
    }

    @Transactional(readOnly = true)
    public List<ContentSummaryDTO> listMyBookmarks(String userId) {
        ensureLoggedIn(userId);
        return bookmarkRepo.findAllWithContentByUserId(userId)
                .stream()
                .map(cb -> ContentSummaryDTO.from(cb.getContent()))
                .toList();
    }

    @Transactional(readOnly = true)
    public long countByContent(Long contentId) {
        if (!contentRepo.existsById(contentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "content not found");
        }
        return bookmarkRepo.countByIdContentId(contentId);
    }

    @Transactional(readOnly = true)
    public boolean isBookmarked(String userId, Long contentId) {
        ensureLoggedIn(userId);
        if (!contentRepo.existsById(contentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "content not found");
        }
        return bookmarkRepo.existsByIdUserIdAndIdContentId(userId, contentId);
    }

    private void ensureLoggedIn(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "login required");
        }
    }
}
