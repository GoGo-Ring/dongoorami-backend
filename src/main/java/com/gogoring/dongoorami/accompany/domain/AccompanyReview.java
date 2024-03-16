package com.gogoring.dongoorami.accompany.domain;

import com.gogoring.dongoorami.global.common.BaseEntity;
import com.gogoring.dongoorami.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class AccompanyReview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "accompany_post_id")
    private AccompanyPost accompanyPost;
    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private Member reviewer;
    @ManyToOne
    @JoinColumn(name = "reviewee_id")
    private Member reviewee;
    private String content;
    private Integer rating;
    @Enumerated(EnumType.STRING)
    private final AccompanyReviewStatusType status = AccompanyReviewStatusType.BEFORE_ACCOMPANY;

    @Builder
    private AccompanyReview(AccompanyPost accompanyPost, Member reviewer, Member reviewee) {
        this.accompanyPost = accompanyPost;
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        reviewer.addReviewForWrite(this);
        reviewee.addReviewForReceive(this);
    }

    public void setAccompanyPost(AccompanyPost accompanyPost) {
        this.accompanyPost = accompanyPost;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public enum AccompanyReviewStatusType {
        BEFORE_ACCOMPANY("동행 전"),
        AFTER_ACCOMPANY_AND_WRITTEN("작성 완료"),
        AFTER_ACCOMPANY_AND_NOT_WRITTEN("작성 필요");

        String name;

        AccompanyReviewStatusType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
