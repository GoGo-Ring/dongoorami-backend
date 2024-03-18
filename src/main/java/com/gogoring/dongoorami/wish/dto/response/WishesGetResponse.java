package com.gogoring.dongoorami.wish.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WishesGetResponse {

    private final Boolean hasNext;

    private final List<WishGetResponse> wishGetResponses;

    @Builder
    public WishesGetResponse(Boolean hasNext, List<WishGetResponse> wishGetResponses) {
        this.hasNext = hasNext;
        this.wishGetResponses = wishGetResponses;
    }

    public static WishesGetResponse of(Boolean hasNext, List<WishGetResponse> wishGetResponses) {
        return WishesGetResponse.builder()
                .hasNext(hasNext)
                .wishGetResponses(wishGetResponses)
                .build();
    }
}
