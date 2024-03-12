package com.gogoring.dongoorami.concert.dto.response;

import com.gogoring.dongoorami.concert.domain.ConcertReview;
import com.gogoring.dongoorami.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ConcertReviewGetResponse {

    private final Long id;

    private final String title;

    private final String content;

    private final Integer rating;

    private final Boolean isWriter;

    @Builder
    public ConcertReviewGetResponse(Long id, String title, String content, Integer rating,
            Boolean isWriter) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.isWriter = isWriter;
    }

    public static ConcertReviewGetResponse of(ConcertReview concertReview, Member member) {
        return ConcertReviewGetResponse.builder()
                .id(concertReview.getId())
                .title(concertReview.getTitle())
                .content(concertReview.getContent())
                .rating(concertReview.getRating())
                .isWriter(concertReview.getMember().getId().equals(member.getId()))
                .build();
    }
}
