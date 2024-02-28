package com.gogoring.dongoorami.accompany.dto.response;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccompanyPostsResponse {

    private Boolean hasNext;
    private List<AccompanyPostInfo> accompanyPostInfos;

    @Builder
    @Getter
    @AllArgsConstructor
    public static class AccompanyPostInfo {

        private Long id;
        private String title;
        private String writer;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String status;
        private String concertName;
        private Long viewCount;
        private Long commentCount;
        private String gender;
        private Long totalPeople;

        public static AccompanyPostInfo of(AccompanyPost accompanyPost) {
            return AccompanyPostInfo.builder()
                    .id(accompanyPost.getId())
                    .title(accompanyPost.getTitle())
                    .gender(accompanyPost.getGender())
                    .concertName(accompanyPost.getConcertName())
                    .status(accompanyPost.getStatus().getName())
                    .totalPeople(accompanyPost.getTotalPeople())
                    .createdAt(accompanyPost.getCreatedAt())
                    .updatedAt(accompanyPost.getUpdatedAt())
                    .writer(accompanyPost.getMember().getName())
                    .viewCount(accompanyPost.getViewCount())
                    .commentCount(0L) // 임시
                    .build();
        }
    }
}
