package com.gogoring.dongoorami.concert.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ConcertReviewsGetResponse {

    private final Boolean hasNext;

    private final List<ConcertReviewGetResponse> concertReviewGetResponses;

    @Builder
    public ConcertReviewsGetResponse(Boolean hasNext,
            List<ConcertReviewGetResponse> concertReviewGetResponses) {
        this.hasNext = hasNext;
        this.concertReviewGetResponses = concertReviewGetResponses;
    }

    public static ConcertReviewsGetResponse of(Boolean hasNext,
            List<ConcertReviewGetResponse> concertReviewGetResponses) {
        return ConcertReviewsGetResponse.builder()
                .hasNext(hasNext)
                .concertReviewGetResponses(concertReviewGetResponses)
                .build();
    }
}
