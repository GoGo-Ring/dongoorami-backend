package com.gogoring.dongoorami.concert.application;

import com.gogoring.dongoorami.concert.dto.request.ConcertReviewCreateRequest;

public interface ConcertService {

    void createConcertReview(Long concertId, ConcertReviewCreateRequest concertReviewCreateRequest,
            Long memberId);
}
