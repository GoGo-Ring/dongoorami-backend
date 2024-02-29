package com.gogoring.dongoorami.accompany.presentation;

import com.gogoring.dongoorami.accompany.application.AccompanyService;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyCommentRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyCommentsResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsResponse;
import com.gogoring.dongoorami.global.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accompany")
@RequiredArgsConstructor
public class AccompanyController {

    private final AccompanyService accompanyService;

    @GetMapping("/posts")
    public ResponseEntity<AccompanyPostsResponse> getAccompanyPosts(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(accompanyService.getAccompanyPosts(cursorId, size));
    }

    @GetMapping("/posts/{accompanyPostId}")
    public ResponseEntity<AccompanyPostResponse> getAccompanyPosts(
            @PathVariable Long accompanyPostId) {
        return ResponseEntity.ok(accompanyService.getAccompanyPost(accompanyPostId));
    }

    @PostMapping("/posts")
    public ResponseEntity<Void> createAccompanyPost(
            @Valid AccompanyPostRequest accompanyPostRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long accompanyPostId = accompanyService.createAccompanyPost(accompanyPostRequest,
                customUserDetails.getId());
        return ResponseEntity.created(URI.create("/api/v1/accompany/posts/" + accompanyPostId))
                .build();
    }

    @PostMapping("/comments/{accompanyPostId}")
    public ResponseEntity<Void> createAccompanyPostComment(
            @PathVariable Long accompanyPostId,
            @Valid @RequestBody AccompanyCommentRequest accompanyCommentRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        accompanyService.createAccompanyComment(accompanyPostId, accompanyCommentRequest,
                customUserDetails.getId());
        return ResponseEntity.created(URI.create("/api/v1/accompany/posts/" + accompanyPostId))
                .build();
    }

    @GetMapping("/comments/{accompanyPostId}")
    public ResponseEntity<AccompanyCommentsResponse> getAccompanyComments(
            @PathVariable Long accompanyPostId) {
        return ResponseEntity.ok(accompanyService.getAccompanyComments(accompanyPostId));
    }

}
