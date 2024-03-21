package com.gogoring.dongoorami.accompany.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AccompanyCommentsShortResponse {

    private final Boolean hasNext;

    private final List<AccompanyCommentShortResponse> accompanyCommentShortResponses;

    @Builder
    public AccompanyCommentsShortResponse(Boolean hasNext,
            List<AccompanyCommentShortResponse> accompanyCommentShortResponses) {
        this.hasNext = hasNext;
        this.accompanyCommentShortResponses = accompanyCommentShortResponses;
    }

    public static AccompanyCommentsShortResponse of(Boolean hasNext,
            List<AccompanyCommentShortResponse> accompanyCommentShortResponses) {
        return AccompanyCommentsShortResponse.builder()
                .hasNext(hasNext)
                .accompanyCommentShortResponses(accompanyCommentShortResponses)
                .build();
    }
}
