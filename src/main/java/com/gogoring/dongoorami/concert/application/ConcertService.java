package com.gogoring.dongoorami.concert.application;

import com.gogoring.dongoorami.accompany.dto.response.ReviewResponse;
import com.gogoring.dongoorami.concert.dto.request.ConcertReviewRequest;
import com.gogoring.dongoorami.concert.dto.response.AccompanyPostsAndConcertsResponse;
import com.gogoring.dongoorami.concert.dto.response.ConcertGetImagesResponse;
import com.gogoring.dongoorami.concert.dto.response.ConcertGetResponse;
import com.gogoring.dongoorami.concert.dto.response.ConcertInfoResponse;
import com.gogoring.dongoorami.concert.dto.response.ConcertReviewsGetResponse;
import com.gogoring.dongoorami.concert.dto.response.ConcertsGetShortResponse;
import java.util.List;

public interface ConcertService {

    void createConcertReview(Long concertId, ConcertReviewRequest concertReviewRequest,
            Long memberId);

    ConcertReviewsGetResponse getConcertReviews(Long concertId, Long cursorId, int size,
            Long memberId);

    void updateConcertReview(Long concertReviewId, ConcertReviewRequest concertReviewRequest,
            Long memberId);

    void deleteConcertReview(Long concertReviewId, Long memberId);

    ConcertGetResponse getConcert(Long concertId);

    ConcertsGetShortResponse getConcerts(Long cursorId, int size, String keyword,
            List<String> genres,
            List<String> statuses);

    List<ConcertInfoResponse> getConcertsByKeyword(String keyword);

    List<ReviewResponse> getConcertAndAccompanyReview(Long memberId);

    List<ConcertGetImagesResponse> getConcertImages();

    AccompanyPostsAndConcertsResponse getAccompanyPostsAndConcertsByKeyword(
            Long accompanyPostCursorId, Long concertCursorId, int size, String keyword,
            Long currentMemberId);
}
