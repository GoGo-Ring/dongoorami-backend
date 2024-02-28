package com.gogoring.dongoorami.accompany.dto.response;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.member.domain.Member;
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
    private MemberInfo writer;
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

    public static AccompanyPostResponse of(AccompanyPost accompanyPost, MemberInfo writer) {
        return AccompanyPostResponse.builder()
                .id(accompanyPost.getId())
                .title(accompanyPost.getTitle())
                .gender(accompanyPost.getGender())
                .concertName(accompanyPost.getConcertName())
                .status(accompanyPost.getStatus().getName())
                .totalPeople(accompanyPost.getTotalPeople())
                .createdAt(accompanyPost.getCreatedAt())
                .updatedAt(accompanyPost.getUpdatedAt())
                .viewCount(accompanyPost.getViewCount())
                .commentCount(0L) // 임시
                .writer(writer)
                .concertPlace(accompanyPost.getConcertPlace())
                .region(accompanyPost.getRegion())
                .startAge(accompanyPost.getStartAge())
                .endAge(accompanyPost.getEndAge())
                .startDate(accompanyPost.getStartDate())
                .endDate(accompanyPost.getEndDate())
                .waitingCount(0L) // 임시
                .content(accompanyPost.getContent())
                .images(accompanyPost.getImages())
                .isWish(true) // 임시
                .build();
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class MemberInfo {

        private Long id;

        private String name;

        private String profileImage;

        private String gender;

        private Integer age;

        private String introduction;

        public static MemberInfo of(Member member) {
            return MemberInfo.builder()
                    .id(member.getId())
                    .age(member.getAge())
                    .introduction(member.getIntroduction())
                    .name(member.getName())
                    .profileImage(member.getProfileImage())
                    .gender(member.getGender()).build();
        }
    }
}
