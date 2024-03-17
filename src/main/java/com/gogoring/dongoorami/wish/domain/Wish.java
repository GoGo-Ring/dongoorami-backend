package com.gogoring.dongoorami.wish.domain;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.global.common.BaseEntity;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.wish.exception.AlreadyWishedException;
import com.gogoring.dongoorami.wish.exception.WishErrorCode;
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
public class Wish extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accompany_post_id")
    private AccompanyPost accompanyPost;

    @Builder
    public Wish(Member member, AccompanyPost accompanyPost) {
        this.member = member;
        this.accompanyPost = accompanyPost;
    }

    public void updateIsActivatedTrue() {
        checkIsActivatedFalse();

        super.updateIsActivatedTrue();
    }

    private void checkIsActivatedFalse() {
        if (this.isActivated()) {
            throw new AlreadyWishedException(WishErrorCode.ALREADY_WISHED);
        }
    }
}
