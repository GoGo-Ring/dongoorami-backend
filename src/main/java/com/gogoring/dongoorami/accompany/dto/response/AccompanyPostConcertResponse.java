package com.gogoring.dongoorami.accompany.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AccompanyPostConcertResponse {

    private final Long id;

    private final String nickname;

    private final String title;

    private final String content;

    private final Long viewCount;

    private final Long commentCount;

    @JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
    private final LocalDate updatedAt;

    @Builder
    public AccompanyPostConcertResponse(Long id, String nickname, String title, String content,
            Long viewCount, Long commentCount, LocalDate updatedAt) {
        this.id = id;
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.updatedAt = updatedAt;
    }

    public static AccompanyPostConcertResponse of(AccompanyPost accompanyPost, Long commentCount) {
        return AccompanyPostConcertResponse.builder()
                .id(accompanyPost.getId())
                .nickname(accompanyPost.getWriter().getNickname())
                .title(accompanyPost.getTitle())
                .content(accompanyPost.getContent())
                .viewCount(accompanyPost.getViewCount())
                .commentCount(commentCount)
                .updatedAt(accompanyPost.getUpdatedAt().toLocalDate())
                .build();
    }
}
