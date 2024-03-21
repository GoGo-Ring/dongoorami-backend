package com.gogoring.dongoorami.concert.presentation;

import com.gogoring.dongoorami.accompany.dto.response.ReviewResponse;
import com.gogoring.dongoorami.concert.application.ConcertService;
import com.gogoring.dongoorami.concert.dto.request.ConcertReviewRequest;
import com.gogoring.dongoorami.concert.dto.response.AccompanyPostsAndConcertsResponse;
import com.gogoring.dongoorami.concert.dto.response.ConcertGetImagesResponse;
import com.gogoring.dongoorami.concert.dto.response.ConcertGetResponse;
import com.gogoring.dongoorami.concert.dto.response.ConcertInfoResponse;
import com.gogoring.dongoorami.concert.dto.response.ConcertReviewsGetResponse;
import com.gogoring.dongoorami.concert.dto.response.ConcertsGetShortResponse;
import com.gogoring.dongoorami.global.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;

    @PostMapping("/concerts/reviews/{concertId}")
    public ResponseEntity<Void> createConcertReview(@PathVariable Long concertId,
            @Valid @RequestBody ConcertReviewRequest concertReviewRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        concertService.createConcertReview(concertId, concertReviewRequest,
                customUserDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/concerts/reviews/{concertId}")
    public ResponseEntity<ConcertReviewsGetResponse> getConcertReviews(@PathVariable Long concertId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(concertService.getConcertReviews(concertId, cursorId, size,
                customUserDetails != null ? customUserDetails.getId() : -1));
    }

    @PatchMapping("/concerts/reviews/{concertReviewId}")
    public ResponseEntity<Void> updateConcertReview(@PathVariable Long concertReviewId,
            @Valid @RequestBody ConcertReviewRequest concertReviewRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        concertService.updateConcertReview(concertReviewId, concertReviewRequest,
                customUserDetails.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/concerts/reviews/{concertReviewId}")
    public ResponseEntity<Void> deleteConcertReview(@PathVariable Long concertReviewId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        concertService.deleteConcertReview(concertReviewId, customUserDetails.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/concerts/{concertId}")
    public ResponseEntity<ConcertGetResponse> getConcert(@PathVariable Long concertId) {
        return ResponseEntity.ok(concertService.getConcert(concertId));
    }

    @GetMapping("/concerts")
    public ResponseEntity<ConcertsGetShortResponse> getConcerts(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false, defaultValue = "6") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> genres,
            @RequestParam(required = false) List<String> statuses) {
        return ResponseEntity.ok(
                concertService.getConcerts(cursorId, size, keyword, genres, statuses));
    }

    @GetMapping("/concerts/keywords")
    public ResponseEntity<List<ConcertInfoResponse>> getConcertsByKeyword(
            @RequestParam String keyword) {
        return ResponseEntity.ok(concertService.getConcertsByKeyword(keyword));
    }

    @GetMapping("/concerts/accompanies/reviews")
    public ResponseEntity<List<ReviewResponse>> getConcertAndAccompanyReviews(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(concertService.getConcertAndAccompanyReview(
                customUserDetails.getId()));
    }

    @GetMapping("/concerts/images")
    public ResponseEntity<List<ConcertGetImagesResponse>> getConcertImages() {
        return ResponseEntity.ok(concertService.getConcertImages());
    }

    @GetMapping("/accompanies-concerts")
    public ResponseEntity<AccompanyPostsAndConcertsResponse> getAccompanyPostsAndConcertsByKeyword(
            @RequestParam(required = false) Long accompanyPostCursorId,
            @RequestParam(required = false) Long concertCursorId,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(
                concertService.getAccompanyPostsAndConcertsByKeyword(accompanyPostCursorId,
                        concertCursorId, size, keyword,
                        customUserDetails != null ? customUserDetails.getId() : -1));
    }
}
