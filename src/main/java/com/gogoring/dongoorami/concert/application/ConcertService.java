package com.gogoring.dongoorami.concert.application;

import com.gogoring.dongoorami.concert.dto.request.ConcertReviewRequest;
import com.gogoring.dongoorami.concert.dto.response.ConcertReviewsGetResponse;
import com.gogoring.dongoorami.concert.dto.response.ConcertInfoResponse;
import java.util.List;

public interface ConcertService {

    void createConcertReview(Long concertId, ConcertReviewRequest concertReviewRequest,
            Long memberId);

    ConcertReviewsGetResponse getConcertReviews(Long concertId, Long cursorId, int size,
            Long memberId);

    void updateConcertReview(Long concertReviewId, ConcertReviewRequest concertReviewRequest,
            Long memberId);

    void deleteConcertReview(Long concertReviewId, Long memberId);

    List<ConcertInfoResponse> getConcertsByKeyword(String keyword);
}
