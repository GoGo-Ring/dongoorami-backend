package com.gogoring.dongoorami.accompany.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.domain.AccompanyReview;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AccompanyReviewResponse {

    private final Long accompanyReviewId;

    private final String content;

    private final Long accompanyPostId;

    private final String title;

    @JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
    private final LocalDate updatedAt;

    @Builder
    public AccompanyReviewResponse(Long accompanyReviewId, String content, Long accompanyPostId,
            String title, LocalDate updatedAt) {
        this.accompanyReviewId = accompanyReviewId;
        this.content = content;
        this.accompanyPostId = accompanyPostId;
        this.title = title;
        this.updatedAt = updatedAt;
    }

    public static AccompanyReviewResponse of(AccompanyReview accompanyReview) {
        AccompanyPost accompanyPost = accompanyReview.getAccompanyPost();

        return AccompanyReviewResponse.builder()
                .accompanyReviewId(accompanyReview.getId())
                .content(accompanyReview.getContent())
                .accompanyPostId(accompanyPost.getId())
                .title(accompanyPost.getTitle())
                .updatedAt(accompanyReview.getUpdatedAt().toLocalDate())
                .build();
    }
}
