package com.gogoring.dongoorami.accompany.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AccompanyCommentShortResponse {

    private final Long accompanyCommentId;

    private final Long accompanyPostId;

    private final String accompanyPostTitle;

    private final String content;

    @JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
    private final LocalDate updatedAt;

    @Builder
    public AccompanyCommentShortResponse(Long accompanyCommentId, Long accompanyPostId,
            String accompanyPostTitle, String content, LocalDate updatedAt) {
        this.accompanyCommentId = accompanyCommentId;
        this.accompanyPostId = accompanyPostId;
        this.accompanyPostTitle = accompanyPostTitle;
        this.content = content;
        this.updatedAt = updatedAt;
    }

    public static AccompanyCommentShortResponse of(AccompanyComment accompanyComment) {
        AccompanyPost accompanyPost = accompanyComment.getAccompanyPost();

        return AccompanyCommentShortResponse.builder()
                .accompanyCommentId(accompanyComment.getId())
                .accompanyPostId(accompanyPost.getId())
                .accompanyPostTitle(accompanyPost.getTitle())
                .content(accompanyComment.getContent())
                .updatedAt(accompanyComment.getUpdatedAt().toLocalDate())
                .build();
    }
}
