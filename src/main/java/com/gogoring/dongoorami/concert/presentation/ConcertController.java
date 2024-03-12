package com.gogoring.dongoorami.concert.presentation;

import com.gogoring.dongoorami.concert.application.ConcertService;
import com.gogoring.dongoorami.concert.dto.request.ConcertReviewCreateRequest;
import com.gogoring.dongoorami.concert.dto.response.ConcertReviewsGetResponse;
import com.gogoring.dongoorami.global.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;

    @PostMapping("/concerts/reviews/{concertId}")
    public ResponseEntity<Void> createConcertReview(@PathVariable Long concertId,
            @Valid @RequestBody ConcertReviewCreateRequest concertReviewCreateRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        concertService.createConcertReview(concertId, concertReviewCreateRequest,
                customUserDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/concerts/reviews/{concertId}")
    public ResponseEntity<ConcertReviewsGetResponse> getConcertReviews(@PathVariable Long concertId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(concertService.getConcertReviews(concertId, cursorId, size,
                customUserDetails.getId()));
    }
}
