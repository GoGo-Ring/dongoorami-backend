package com.gogoring.dongoorami.wish.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.wish.domain.Wish;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WishGetResponse {

    private final Long wishId;

    private final Long accompanyPostId;

    private final String title;

    private final String content;

    private final Long totalPeople;

    @JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
    private final LocalDate updatedAt;

    @Builder
    public WishGetResponse(Long wishId, Long accompanyPostId, String title, String content,
            Long totalPeople, LocalDate updatedAt) {
        this.wishId = wishId;
        this.accompanyPostId = accompanyPostId;
        this.title = title;
        this.content = content;
        this.totalPeople = totalPeople;
        this.updatedAt = updatedAt;
    }

    public static WishGetResponse of(Wish wish) {
        AccompanyPost accompanyPost = wish.getAccompanyPost();

        return WishGetResponse.builder()
                .wishId(wish.getId())
                .accompanyPostId(accompanyPost.getId())
                .title(accompanyPost.getTitle())
                .content(accompanyPost.getContent())
                .totalPeople(accompanyPost.getTotalPeople())
                .updatedAt(accompanyPost.getUpdatedAt().toLocalDate())
                .build();
    }
}
