package com.gogoring.dongoorami.wish.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.gogoring.dongoorami.accompany.AccompanyDataFactory;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.concert.ConcertDataFactory;
import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.member.MemberDataFactory;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.wish.WishDataFactory;
import com.gogoring.dongoorami.wish.exception.AlreadyWishedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WishTest {

    @Test
    @DisplayName("이미 찜한 동행 구인글에 대한 찜하기는 예외를 발생시킨다.")
    void fail_updateIsActivatedTrue() {
        // given
        Member member = MemberDataFactory.createMember();
        Concert concert = ConcertDataFactory.createConcert();
        AccompanyPost accompanyPost = AccompanyDataFactory.createAccompanyPosts(member, 1, concert)
                .get(0);

        Wish wish = WishDataFactory.createWish(member, accompanyPost);

        // when - then
        assertThatThrownBy(wish::updateIsActivatedTrue).isInstanceOf(AlreadyWishedException.class);
    }
}
