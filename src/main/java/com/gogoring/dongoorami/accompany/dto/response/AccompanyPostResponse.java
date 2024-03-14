package com.gogoring.dongoorami.accompany.dto.response;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost.AccompanyPurposeType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccompanyPostResponse {

    private Long id;
    private String title;
    private MemberProfile memberProfile;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long viewCount;
    private Long commentCount;
    private String status;
    private String concertName;
    private String concertPlace;
    private String region;
    private Long startAge;
    private Long endAge;
    private Long totalPeople;
    private String gender;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long waitingCount;
    private String content;
    private List<String> images;
    private Boolean isWish;
    private Boolean isWriter;
    private List<String> purposes;

    public static AccompanyPostResponse of(AccompanyPost accompanyPost, Long waitingCount,
            MemberProfile memberProfile) {
        return AccompanyPostResponse.builder()
                .id(accompanyPost.getId())
                .title(accompanyPost.getTitle())
                .gender(accompanyPost.getGender())
                .concertName(accompanyPost.getConcert().getName())
                .status(accompanyPost.getStatus().getName())
                .totalPeople(accompanyPost.getTotalPeople())
                .createdAt(accompanyPost.getCreatedAt())
                .updatedAt(accompanyPost.getUpdatedAt())
                .viewCount(accompanyPost.getViewCount())
                .commentCount(0L) // 임시
                .memberProfile(memberProfile)
                .concertPlace(accompanyPost.getConcert().getPlace())
                .region(accompanyPost.getRegion().getName())
                .startAge(accompanyPost.getStartAge())
                .endAge(accompanyPost.getEndAge())
                .startDate(accompanyPost.getStartDate())
                .endDate(accompanyPost.getEndDate())
                .waitingCount(waitingCount)
                .content(accompanyPost.getContent())
                .images(accompanyPost.getImages())
                .isWish(true) // 임시
                .isWriter(memberProfile.isCurrentMember())
                .purposes(accompanyPost.getPurposes().stream().map(AccompanyPurposeType::getName)
                        .toList())
                .build();
    }
}
