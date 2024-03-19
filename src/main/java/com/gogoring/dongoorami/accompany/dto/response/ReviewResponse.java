package com.gogoring.dongoorami.accompany.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.domain.AccompanyReview;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewResponse {

    private final Long reviewId;

    private final String content;

    private final Long targetId;

    private final String title;

    @JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
    private final LocalDate updatedAt;

    private final Boolean isAccompanyReview;

    @Builder
    public ReviewResponse(Long reviewId, String content, Long targetId,
            String title, LocalDate updatedAt, Boolean isAccompanyReview) {
        this.reviewId = reviewId;
        this.content = content;
        this.targetId = targetId;
        this.title = title;
        this.updatedAt = updatedAt;
        this.isAccompanyReview = isAccompanyReview;
    }

    public static ReviewResponse of(AccompanyReview accompanyReview) {
        AccompanyPost accompanyPost = accompanyReview.getAccompanyPost();

        return ReviewResponse.builder()
                .reviewId(accompanyReview.getId())
                .content(accompanyReview.getContent())
                .targetId(accompanyPost.getId())
                .title(accompanyPost.getTitle())
                .updatedAt(accompanyReview.getUpdatedAt().toLocalDate())
                .isAccompanyReview(true)
                .build();
    }
}
