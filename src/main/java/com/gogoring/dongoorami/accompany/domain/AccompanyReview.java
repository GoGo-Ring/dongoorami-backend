package com.gogoring.dongoorami.accompany.domain;

import com.gogoring.dongoorami.accompany.dto.request.AccompanyReviewRequest;
import com.gogoring.dongoorami.accompany.exception.AccompanyErrorCode;
import com.gogoring.dongoorami.accompany.exception.InvalidAccompanyRegionTypeException;
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
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class AccompanyReview extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private AccompanyReviewStatusType status = AccompanyReviewStatusType.BEFORE_ACCOMPANY;
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
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<RatingItemType> ratingItemTypes;

    @Builder
    private AccompanyReview(AccompanyPost accompanyPost, Member reviewer, Member reviewee) {
        this.accompanyPost = accompanyPost;
        this.reviewer = reviewer;
        this.reviewee = reviewee;
    }

    public void update(AccompanyReviewRequest accompanyReviewRequest) {
        this.content = accompanyReviewRequest.getContent();
        this.rating = accompanyReviewRequest.getRating();
        this.ratingItemTypes = accompanyReviewRequest.getRatingItemTypes().stream()
                .map(RatingItemType::getValue).toList();
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateStatus(AccompanyReviewStatusType status) {
        this.status = status;
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

    public enum RatingItemType {

        ON_TIME("시간 약속을 잘 지켜요."),
        QUICK_RESPONSE("응답이 빨라요."),
        KINDNESS("친절하고 매너가 좋아요."),
        ACCURATE_SETTLEMENT("정산이 확실해요.");

        String name;

        RatingItemType(String name) {
            this.name = name;
        }

        public static RatingItemType getValue(String name) {
            return Arrays.stream(RatingItemType.values()).filter(
                            ratingItemType -> ratingItemType.getName().equals(name)).findAny()
                    .orElseThrow(() -> new InvalidAccompanyRegionTypeException(
                            AccompanyErrorCode.INVALID_RATING_ITEM_TYPE));
        }

        public String getName() {
            return name;
        }

    }
}
