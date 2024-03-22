package com.gogoring.dongoorami.concert.dto.response;

import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsResponse.AccompanyPostInfo;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AccompanyPostsAndConcertsResponse {

    private final Boolean hasNextAccompanyPost;

    private final Boolean hasNextConcert;

    private final List<AccompanyPostInfo> accompanyPostInfos;

    private final List<ConcertGetShortResponse> concertGetShortResponses;

    @Builder
    public AccompanyPostsAndConcertsResponse(Boolean hasNextAccompanyPost,
            Boolean hasNextConcert,
            List<AccompanyPostInfo> accompanyPostInfos,
            List<ConcertGetShortResponse> concertGetShortResponses) {
        this.hasNextAccompanyPost = hasNextAccompanyPost;
        this.hasNextConcert = hasNextConcert;
        this.accompanyPostInfos = accompanyPostInfos;
        this.concertGetShortResponses = concertGetShortResponses;
    }


    public static AccompanyPostsAndConcertsResponse of(Boolean hasNextAccompanyPost,
            Boolean hasNextConcert,
            List<AccompanyPostInfo> accompanyPostInfos,
            List<ConcertGetShortResponse> concertGetShortResponses) {
        return AccompanyPostsAndConcertsResponse.builder()
                .hasNextAccompanyPost(hasNextAccompanyPost)
                .hasNextConcert(hasNextConcert)
                .accompanyPostInfos(accompanyPostInfos)
                .concertGetShortResponses(concertGetShortResponses)
                .build();
    }
}
