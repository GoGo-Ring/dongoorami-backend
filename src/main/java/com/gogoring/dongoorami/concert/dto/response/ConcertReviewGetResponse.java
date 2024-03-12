package com.gogoring.dongoorami.concert.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogoring.dongoorami.concert.domain.ConcertReview;
import com.gogoring.dongoorami.member.domain.Member;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ConcertReviewGetResponse {

    private final Long id;

    private final String nickname;

    private final String title;

    private final String content;

    private final Integer rating;

    private final Boolean isWriter;

    @JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
    private final LocalDate updatedAt;

    @Builder
    public ConcertReviewGetResponse(Long id, String nickname, String title, String content,
            Integer rating, Boolean isWriter, LocalDate updatedAt) {
        this.id = id;
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.isWriter = isWriter;
        this.updatedAt = updatedAt;
    }

    public static ConcertReviewGetResponse of(ConcertReview concertReview, Member member) {
        return ConcertReviewGetResponse.builder()
                .id(concertReview.getId())
                .nickname(member.getNickname())
                .title(concertReview.getTitle())
                .content(concertReview.getContent())
                .rating(concertReview.getRating())
                .isWriter(concertReview.getMember().getId().equals(member.getId()))
                .updatedAt(concertReview.getUpdatedAt().toLocalDate())
                .build();
    }
}
