package com.gogoring.dongoorami.concert.application;

import com.gogoring.dongoorami.concert.dto.request.ConcertReviewCreateRequest;
import com.gogoring.dongoorami.concert.dto.response.ConcertReviewsGetResponse;

public interface ConcertService {

    void createConcertReview(Long concertId, ConcertReviewCreateRequest concertReviewCreateRequest,
            Long memberId);

    ConcertReviewsGetResponse getConcertReviews(Long concertId, Long cursorId, int size,
            Long memberId);
}
