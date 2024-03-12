package com.gogoring.dongoorami.concert.domain;

import com.gogoring.dongoorami.concert.exception.ConcertErrorCode;
import com.gogoring.dongoorami.concert.exception.ConcertReviewModifyDeniedException;
import com.gogoring.dongoorami.global.common.BaseEntity;
import com.gogoring.dongoorami.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class ConcertReview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer rating;

    @Builder
    public ConcertReview(Concert concert, Member member, String title, String content,
            Integer rating) {
        this.concert = concert;
        this.member = member;
        this.title = title;
        this.content = content;
        this.rating = rating;
    }

    public void updateConcertReview(Long memberId, String title, String content, Integer rating) {
        checkIsWriter(memberId);

        this.title = title;
        this.content = content;
        this.rating = rating;
    }

    private void checkIsWriter(Long memberId) {
        if (!this.member.getId().equals(memberId)) {
            throw new ConcertReviewModifyDeniedException(
                    ConcertErrorCode.CONCERT_REVIEW_MODIFY_DENIED);
        }
    }
}
