package com.gogoring.dongoorami.accompany.domain;

import com.gogoring.dongoorami.accompany.exception.AccompanyErrorCode;
import com.gogoring.dongoorami.accompany.exception.InvalidAccompanyPurposeTypeException;
import com.gogoring.dongoorami.accompany.exception.InvalidAccompanyRegionTypeException;
import com.gogoring.dongoorami.accompany.exception.OnlyWriterCanModifyException;
import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.global.common.BaseEntity;
import com.gogoring.dongoorami.member.domain.Member;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class AccompanyPost extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private RecruitmentStatusType status = RecruitmentStatusType.PROCEEDING;
    private Long viewCount = 0L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "writer_id")
    private Member writer;
    private String title;
    @ManyToOne
    @JoinColumn(name = "concert_id")
    private Concert concert;
    @Enumerated(EnumType.STRING)
    private AccompanyRegionType region;
    private Long startAge;
    private Long endAge;
    private Long totalPeople;
    private String gender;
    private LocalDate startDate;
    private LocalDate endDate;
    private String content;
    @ElementCollection
    private List<String> images;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<AccompanyPurposeType> purposes;

    @Builder
    public AccompanyPost(Member writer, String title, Concert concert,
            String region, Long startAge, Long endAge, Long totalPeople, String gender,
            LocalDate startDate, LocalDate endDate, String content, List<String> images,
            List<AccompanyPurposeType> purposes) {
        this.writer = writer;
        this.title = title;
        this.concert = concert;
        this.region = AccompanyRegionType.getValue(region);
        this.startAge = startAge;
        this.endAge = endAge;
        this.totalPeople = totalPeople;
        this.gender = gender;
        this.startDate = startDate;
        this.endDate = endDate;
        this.content = content;
        this.images = images;
        this.purposes = purposes;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void update(AccompanyPost accompanyPost, Long memberId) {
        checkIsWriter(memberId);
        this.title = accompanyPost.title;
        this.concert = accompanyPost.concert;
        this.region = accompanyPost.region;
        this.startAge = accompanyPost.startAge;
        this.endAge = accompanyPost.endAge;
        this.totalPeople = accompanyPost.totalPeople;
        this.gender = accompanyPost.gender;
        this.startDate = accompanyPost.startDate;
        this.endDate = accompanyPost.endDate;
        this.content = accompanyPost.content;
        this.images = accompanyPost.images;
        this.purposes = accompanyPost.purposes;
    }

    public void updateStatus() {
        this.status = RecruitmentStatusType.COMPLETED;
    }

    public void updateStatus(Long memberId) {
        checkIsWriter(memberId);
        this.status = RecruitmentStatusType.COMPLETED;
    }

    private void checkIsWriter(Long memberId) {
        if (!this.writer.getId().equals(memberId)) {
            throw new OnlyWriterCanModifyException(AccompanyErrorCode.ONLY_WRITER_CAN_MODIFY);
        }
    }

    public enum RecruitmentStatusType {
        PROCEEDING("모집 중"), COMPLETED("모집 완료");

        String name;

        RecruitmentStatusType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum AccompanyPurposeType {
        ENJOY("관람"), ACCOMMODATION("숙박"), TRANSPORTATION("이동");

        String name;

        AccompanyPurposeType(String name) {
            this.name = name;
        }

        public static AccompanyPurposeType getValue(String name) {
            return Arrays.stream(AccompanyPurposeType.values()).filter(
                            accompanyPurposeType -> accompanyPurposeType.getName().equals(name)).findAny()
                    .orElseThrow(() -> new InvalidAccompanyPurposeTypeException(
                            AccompanyErrorCode.INVALID_ACCOMPANY_PURPOSE_TYPE));
        }

        public String getName() {
            return name;
        }
    }

    public enum AccompanyRegionType {
        CAPITAL_AREA("수도권(경기, 인천 포함)"),
        GANGWON("강원도"),
        CHUNGCHEONG("충청북도/충청남도"),
        GYEONGSANG("경상북도/경상남도"),
        JEOLLA("전라북도/전라남도"),
        JEJU("제주도");

        String name;

        AccompanyRegionType(String name) {
            this.name = name;
        }

        public static AccompanyRegionType getValue(String name) {
            return Arrays.stream(AccompanyRegionType.values()).filter(
                            regionType -> regionType.getName().equals(name)).findAny()
                    .orElseThrow(() -> new InvalidAccompanyRegionTypeException(
                            AccompanyErrorCode.INVALID_REGION_TYPE));
        }

        public static List<String> getNames() {
            return Arrays.stream(AccompanyRegionType.values()).map(AccompanyRegionType::getName)
                    .toList();
        }

        public String getName() {
            return name;
        }
    }
}
