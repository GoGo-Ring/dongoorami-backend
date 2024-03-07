package com.gogoring.dongoorami.accompany.presentation;

import com.gogoring.dongoorami.accompany.application.AccompanyService;
import com.gogoring.dongoorami.accompany.domain.AccompanyRegionType;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyCommentRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostFilterRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyCommentsResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsResponse;
import com.gogoring.dongoorami.global.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/accompany")
@RequiredArgsConstructor
public class AccompanyController {

    private final AccompanyService accompanyService;

    @GetMapping("/posts")
    public ResponseEntity<AccompanyPostsResponse> getAccompanyPosts(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) Long startAge,
            @RequestParam(required = false) Long endAge,
            @RequestParam(required = false) Long totalPeople,
            @RequestParam(required = false) String concertPlace,
            @RequestParam(required = false) List<String> purposes) {
        return ResponseEntity.ok(
                accompanyService.getAccompanyPosts(cursorId, size,
                        AccompanyPostFilterRequest.builder()
                                .gender(gender)
                                .region(region)
                                .startAge(startAge)
                                .endAge(endAge)
                                .totalPeople(totalPeople)
                                .concertPlace(concertPlace)
                                .purposes(purposes)
                                .build()));
    }

    @GetMapping("/posts/{accompanyPostId}")
    public ResponseEntity<AccompanyPostResponse> getAccompanyPost(
            @PathVariable Long accompanyPostId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(accompanyService.getAccompanyPost(customUserDetails.getId(), accompanyPostId));
    }

    @PostMapping("/posts")
    public ResponseEntity<Void> createAccompanyPost(
            @Valid @RequestPart AccompanyPostRequest accompanyPostRequest,
            @RequestPart List<MultipartFile> images,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long accompanyPostId = accompanyService.createAccompanyPost(accompanyPostRequest, images,
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
    public ResponseEntity<AccompanyCommentsResponse> getAccompanyPostComments(
            @PathVariable Long accompanyPostId) {
        return ResponseEntity.ok(accompanyService.getAccompanyComments(accompanyPostId));
    }

    @PostMapping("/posts/{accompanyPostId}")
    public ResponseEntity<Void> updateAccompanyPost(
            @Valid @RequestPart AccompanyPostRequest accompanyPostRequest,
            @RequestPart List<MultipartFile> images,
            @PathVariable Long accompanyPostId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        accompanyService.updateAccompanyPost(accompanyPostRequest, images, customUserDetails.getId(),
                accompanyPostId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{accompanyPostId}")
    public ResponseEntity<Void> deleteAccompanyPost(
            @PathVariable Long accompanyPostId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        accompanyService.deleteAccompanyPost(customUserDetails.getId(), accompanyPostId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts/regions")
    public ResponseEntity<Map<String, Object>> getAccompanyPostRegions() {
        return ResponseEntity.ok(Map.of("regions", AccompanyRegionType.getNames()));
    }

}
