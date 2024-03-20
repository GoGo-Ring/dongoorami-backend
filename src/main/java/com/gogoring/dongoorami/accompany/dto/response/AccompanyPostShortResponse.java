package com.gogoring.dongoorami.accompany.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AccompanyPostShortResponse {

    private final Long id;

    private final String title;

    private final String content;

    private final Long totalPeople;

    @JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
    private final LocalDate updatedAt;

    @Builder
    public AccompanyPostShortResponse(Long id, String title, String content,
            Long totalPeople, LocalDate updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.totalPeople = totalPeople;
        this.updatedAt = updatedAt;
    }

    public static AccompanyPostShortResponse of(AccompanyPost accompanyPost) {
        return AccompanyPostShortResponse.builder()
                .id(accompanyPost.getId())
                .title(accompanyPost.getTitle())
                .content(accompanyPost.getContent())
                .updatedAt(accompanyPost.getUpdatedAt().toLocalDate())
                .build();
    }
}
