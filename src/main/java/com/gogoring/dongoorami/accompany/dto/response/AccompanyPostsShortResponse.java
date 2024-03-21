package com.gogoring.dongoorami.accompany.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AccompanyPostsShortResponse {

    private final Boolean hasNext;

    private final List<AccompanyPostShortResponse> accompanyPostShortResponses;

    @Builder
    public AccompanyPostsShortResponse(Boolean hasNext,
            List<AccompanyPostShortResponse> accompanyPostShortResponses) {
        this.hasNext = hasNext;
        this.accompanyPostShortResponses = accompanyPostShortResponses;
    }

    public static AccompanyPostsShortResponse of(Boolean hasNext,
            List<AccompanyPostShortResponse> accompanyPostShortResponses) {
        return AccompanyPostsShortResponse.builder()
                .hasNext(hasNext)
                .accompanyPostShortResponses(accompanyPostShortResponses)
                .build();
    }
}
