package com.gogoring.dongoorami.concert.dto.request;

import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.domain.ConcertReview;
import com.gogoring.dongoorami.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
public class ConcertReviewCreateRequest {

    @NotBlank(message = "제목은 공백일 수 없습니다.")
    private String title;

    @NotBlank(message = "내용은 공백일 수 없습니다.")
    private String content;

    @Range(min = 1, max = 5, message = "평점은 1 이상 5 이하의 정수입니다.")
    @NotNull(message = "평점은 공백일 수 없습니다.")
    private Integer rating;

    public ConcertReview toEntity(Concert concert, Member member) {
        return ConcertReview.builder()
                .concert(concert)
                .member(member)
                .title(title)
                .content(content)
                .rating(rating)
                .build();
    }
}
