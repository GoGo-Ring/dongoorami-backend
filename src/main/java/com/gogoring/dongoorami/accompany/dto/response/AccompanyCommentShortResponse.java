package com.gogoring.dongoorami.accompany.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AccompanyCommentShortResponse {

    private final Long id;

    private final String accompanyPostTitle;

    private final String content;

    @JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
    private final LocalDate updatedAt;

    @Builder
    public AccompanyCommentShortResponse(Long id, String accompanyPostTitle, String content,
            LocalDate updatedAt) {
        this.id = id;
        this.accompanyPostTitle = accompanyPostTitle;
        this.content = content;
        this.updatedAt = updatedAt;
    }

    public static AccompanyCommentShortResponse of(AccompanyComment accompanyComment) {
        return AccompanyCommentShortResponse.builder()
                .id(accompanyComment.getId())
                .accompanyPostTitle(accompanyComment.getAccompanyPost().getTitle())
                .content(accompanyComment.getContent())
                .updatedAt(accompanyComment.getUpdatedAt().toLocalDate())
                .build();
    }
}
