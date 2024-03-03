package com.gogoring.dongoorami.accompany.domain;

import com.gogoring.dongoorami.global.common.BaseEntity;
import com.gogoring.dongoorami.member.domain.Member;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private final RecruitmentStatus status = RecruitmentStatus.PROCEEDING;
    private Long viewCount = 0L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Member member;
    private String title;
    private String concertName;
    private String concertPlace;
    private String region;
    private Long startAge;
    private Long endAge;
    private Long totalPeople;
    private String gender;
    private LocalDate startDate;
    private LocalDate endDate;
    private String content;
    @OneToMany(mappedBy = "accompanyPost")
    private final List<AccompanyComment> accompanyComments = new ArrayList<>();

    @ElementCollection
    private List<String> images;

    @Builder
    public AccompanyPost(Member member, String title, String concertName, String concertPlace,
            String region, Long startAge, Long endAge, Long totalPeople, String gender,
            LocalDate startDate, LocalDate endDate, String content, List<String> images) {
        this.member = member;
        this.title = title;
        this.concertName = concertName;
        this.concertPlace = concertPlace;
        this.region = region;
        this.startAge = startAge;
        this.endAge = endAge;
        this.totalPeople = totalPeople;
        this.gender = gender;
        this.startDate = startDate;
        this.endDate = endDate;
        this.content = content;
        this.images = images;
    }

    public void increaseViewCount() {
        this.viewCount ++;
    }

    public void addAccompanyComment(AccompanyComment accompanyComment) {
        accompanyComments.add(accompanyComment);
        accompanyComment.setAccompanyPost(this);
    }

    public void update(AccompanyPost accompanyPost) {
        this.member = accompanyPost.member;
        this.title = accompanyPost.title;
        this.concertName = accompanyPost.concertName;
        this.concertPlace = accompanyPost.concertPlace;
        this.region = accompanyPost.region;
        this.startAge = accompanyPost.startAge;
        this.endAge = accompanyPost.endAge;
        this.totalPeople = accompanyPost.totalPeople;
        this.gender = accompanyPost.gender;
        this.startDate = accompanyPost.startDate;
        this.endDate = accompanyPost.endDate;
        this.content = accompanyPost.content;
        this.images = accompanyPost.images;
    }

    public enum RecruitmentStatus {
        PROCEEDING("모집 중"), COMPLETED("모집 완료");

        String name;

        RecruitmentStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
