package com.gogoring.dongoorami.accompany.presentation;

import com.gogoring.dongoorami.accompany.application.AccompanyService;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost.AccompanyRegionType;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyCommentRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostFilterRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyReviewRequest;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyCommentsResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyCommentsShortResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsConcertResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsShortResponse;
import com.gogoring.dongoorami.accompany.dto.response.MemberProfile;
import com.gogoring.dongoorami.accompany.dto.response.ReviewsResponse;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/accompanies")
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
        return ResponseEntity.ok(
                accompanyService.getAccompanyPost(
                        customUserDetails != null ? customUserDetails.getId() : -1,
                        accompanyPostId));
    }

    @PostMapping("/posts")
    public ResponseEntity<Void> createAccompanyPost(
            @Valid @RequestPart AccompanyPostRequest accompanyPostRequest,
            @RequestPart List<MultipartFile> images,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long accompanyPostId = accompanyService.createAccompanyPost(accompanyPostRequest, images,
                customUserDetails.getId());
        return ResponseEntity.created(URI.create("/api/v1/accompanies/posts/" + accompanyPostId))
                .build();
    }

    @PostMapping("/comments/{accompanyPostId}")
    public ResponseEntity<Void> createAccompanyPostComment(
            @PathVariable Long accompanyPostId,
            @Valid @RequestBody AccompanyCommentRequest accompanyCommentRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        accompanyService.createAccompanyComment(accompanyPostId, accompanyCommentRequest,
                customUserDetails.getId(), false);
        return ResponseEntity.created(URI.create("/api/v1/accompanies/posts/" + accompanyPostId))
                .build();
    }

    @GetMapping("/comments/{accompanyPostId}")
    public ResponseEntity<AccompanyCommentsResponse> getAccompanyPostComments(
            @PathVariable Long accompanyPostId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(
                accompanyService.getAccompanyComments(accompanyPostId,
                        customUserDetails != null ? customUserDetails.getId() : -1));
    }

    @PostMapping("/posts/{accompanyPostId}")
    public ResponseEntity<Void> updateAccompanyPost(
            @Valid @RequestPart AccompanyPostRequest accompanyPostRequest,
            @RequestPart List<MultipartFile> images,
            @PathVariable Long accompanyPostId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        accompanyService.updateAccompanyPost(accompanyPostRequest, images,
                customUserDetails.getId(),
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

    @GetMapping("/profile/{memberId}")
    public ResponseEntity<MemberProfile> getMemberProfile(
            @PathVariable Long memberId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        return ResponseEntity.ok(
                accompanyService.getMemberProfile(memberId, customUserDetails.getId()));
    }

    @PatchMapping("/comments/{accompanyCommentId}")
    public ResponseEntity<Void> updateAccompanyComment(
            @PathVariable Long accompanyCommentId,
            @Valid @RequestBody AccompanyCommentRequest accompanyCommentRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        accompanyService.updateAccompanyComment(accompanyCommentId, accompanyCommentRequest,
                customUserDetails.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comments/{accompanyCommentId}")
    public ResponseEntity<Void> deleteAccompanyComment(
            @PathVariable Long accompanyCommentId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        accompanyService.deleteAccompanyComment(accompanyCommentId, customUserDetails.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accompanyPostId}")
    public ResponseEntity<Void> applyAccompany(
            @PathVariable Long accompanyPostId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        accompanyService.createAccompanyApplyComment(accompanyPostId, customUserDetails.getId());
        return ResponseEntity.created(URI.create("/api/v1/accompanies/posts/" + accompanyPostId))
                .build();
    }

    @PatchMapping("/{accompanyCommentId}")
    public ResponseEntity<Void> confirmAccompany(
            @PathVariable Long accompanyCommentId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        accompanyService.confirmAccompany(accompanyCommentId, customUserDetails.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reviews/reviewees")
    public ResponseEntity<List<MemberProfile>> getReviewees(
            @RequestParam Long accompanyPostId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(
                accompanyService.getReviewees(accompanyPostId, customUserDetails.getId()));
    }

    @PatchMapping("/reviews/{accompanyPostId}")
    public ResponseEntity<Void> updateAccompanyReviews(
            @Valid @RequestBody List<AccompanyReviewRequest> accompanyReviewRequests,
            @PathVariable Long accompanyPostId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        accompanyService.updateAccompanyReview(accompanyReviewRequests, accompanyPostId,
                customUserDetails.getId());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/posts/{accompanyPostId}/status")
    public ResponseEntity<Void> updateAccompanyPostStatus(
            @PathVariable Long accompanyPostId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        accompanyService.updateAccompanyPostStatusCompleted(accompanyPostId,
                customUserDetails.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reviews/reviewees/my-page")
    public ResponseEntity<ReviewsResponse> getReceivedReviews(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(
                accompanyService.getReceivedReviews(cursorId, size, customUserDetails.getId()));
    }

    @GetMapping("/reviews/reviewers/my-page")
    public ResponseEntity<ReviewsResponse> getWaitingReviews(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(
                accompanyService.getWaitingReviews(cursorId, size, customUserDetails.getId()));
    }

    @GetMapping("/posts/my-page")
    public ResponseEntity<AccompanyPostsShortResponse> getAccompanyPostsByMember(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(accompanyService.getAccompanyPostsByMember(cursorId, size,
                customUserDetails.getId()));
    }

    @GetMapping("/comments/my-page")
    public ResponseEntity<AccompanyCommentsShortResponse> getAccompanyCommentsByMember(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(accompanyService.getAccompanyCommentsByMember(cursorId, size,
                customUserDetails.getId()));
    }

    @GetMapping("/reviews/reviewees/{memberId}")
    public ResponseEntity<ReviewsResponse> getReceivedReviews(
            @PathVariable Long memberId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(
                accompanyService.getReceivedReviews(cursorId, size, memberId));
    }

    @GetMapping("/posts/concerts/{concertId}")
    public ResponseEntity<AccompanyPostsConcertResponse> getAccompanyPostsByConcert(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size,
            @PathVariable Long concertId) {
        return ResponseEntity.ok(
                accompanyService.getAccompanyPostsByConcert(cursorId, size, concertId));
    }
}
