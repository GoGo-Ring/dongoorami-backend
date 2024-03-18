package com.gogoring.dongoorami.accompany.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AccompanyReviewsResponse {

    private final Boolean hasNext;

    private final List<AccompanyReviewResponse> accompanyReviewResponses;

    @Builder
    public AccompanyReviewsResponse(Boolean hasNext,
            List<AccompanyReviewResponse> accompanyReviewResponses) {
        this.hasNext = hasNext;
        this.accompanyReviewResponses = accompanyReviewResponses;
    }

    public static AccompanyReviewsResponse of(Boolean hasNext,
            List<AccompanyReviewResponse> accompanyReviewResponses) {
        return AccompanyReviewsResponse.builder()
                .hasNext(hasNext)
                .accompanyReviewResponses(accompanyReviewResponses)
                .build();
    }
}
