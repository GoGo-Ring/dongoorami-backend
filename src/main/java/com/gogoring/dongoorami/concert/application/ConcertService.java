package com.gogoring.dongoorami.concert.application;

import com.gogoring.dongoorami.concert.dto.request.ConcertReviewRequest;
import com.gogoring.dongoorami.concert.dto.response.ConcertReviewsGetResponse;

public interface ConcertService {

    void createConcertReview(Long concertId, ConcertReviewRequest concertReviewRequest,
            Long memberId);

    ConcertReviewsGetResponse getConcertReviews(Long concertId, Long cursorId, int size,
            Long memberId);
}
