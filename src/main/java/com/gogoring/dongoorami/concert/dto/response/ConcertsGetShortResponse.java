package com.gogoring.dongoorami.concert.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ConcertsGetShortResponse {

    private final Boolean hasNext;

    private final List<ConcertGetShortResponse> concertGetShortResponses;

    @Builder
    public ConcertsGetShortResponse(Boolean hasNext,
            List<ConcertGetShortResponse> concertGetShortResponses) {
        this.hasNext = hasNext;
        this.concertGetShortResponses = concertGetShortResponses;
    }

    public static ConcertsGetShortResponse of(Boolean hasNext,
            List<ConcertGetShortResponse> concertGetShortResponses) {
        return ConcertsGetShortResponse.builder()
                .hasNext(hasNext)
                .concertGetShortResponses(concertGetShortResponses)
                .build();
    }
}
