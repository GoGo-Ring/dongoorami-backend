package com.gogoring.dongoorami.accompany.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AccompanyPostsConcertResponse {

    private final Boolean hasNext;

    private final List<AccompanyPostConcertResponse> accompanyPostConcertResponses;

    @Builder
    public AccompanyPostsConcertResponse(Boolean hasNext,
            List<AccompanyPostConcertResponse> accompanyPostConcertResponses) {
        this.hasNext = hasNext;
        this.accompanyPostConcertResponses = accompanyPostConcertResponses;
    }

    public static AccompanyPostsConcertResponse of(Boolean hasNext,
            List<AccompanyPostConcertResponse> accompanyPostConcertResponses) {
        return AccompanyPostsConcertResponse.builder()
                .hasNext(hasNext)
                .accompanyPostConcertResponses(accompanyPostConcertResponses)
                .build();
    }
}
