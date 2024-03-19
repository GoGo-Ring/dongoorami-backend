package com.gogoring.dongoorami.accompany.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewsResponse {

    private final Boolean hasNext;

    private final List<ReviewResponse> reviewResponses;

    @Builder
    public ReviewsResponse(Boolean hasNext,
            List<ReviewResponse> reviewResponses) {
        this.hasNext = hasNext;
        this.reviewResponses = reviewResponses;
    }

    public static ReviewsResponse of(Boolean hasNext,
            List<ReviewResponse> reviewResponses) {
        return ReviewsResponse.builder()
                .hasNext(hasNext)
                .reviewResponses(reviewResponses)
                .build();
    }
}
