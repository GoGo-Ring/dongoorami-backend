package com.gogoring.dongoorami.concert.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.gogoring.dongoorami.concert.exception.ConcertReviewModifyDeniedException;
import com.gogoring.dongoorami.global.util.TestDataUtil;
import com.gogoring.dongoorami.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class ConcertReviewTest {

    @Test
    @DisplayName("수정 권한이 없는 공연 후기에 대한 수정 시도는 예외를 발생시킨다.")
    void fail_updateConcertReview() {
        // given
        Concert concert = TestDataUtil.createConcert();

        Member member1 = TestDataUtil.createMember();
        ReflectionTestUtils.setField(member1, "id", 1L);

        Member member2 = TestDataUtil.createMember();
        ReflectionTestUtils.setField(member2, "id", 2L);

        ConcertReview concertReview = TestDataUtil.createConcertReviews(concert, member1, 1).get(0);

        // when-then
        assertThatThrownBy(
                () -> concertReview.updateConcertReview(member2.getId(), "테스트 제목", "테스트 내용",
                        3)).isInstanceOf(ConcertReviewModifyDeniedException.class);
    }

    @Test
    @DisplayName("삭제 권한이 없는 공연 후기에 대한 삭제 시도는 예외를 발생시킨다.")
    void fail_updateIsActivatedFalse() {
        // given
        Concert concert = TestDataUtil.createConcert();

        Member member1 = TestDataUtil.createMember();
        ReflectionTestUtils.setField(member1, "id", 1L);

        Member member2 = TestDataUtil.createMember();
        ReflectionTestUtils.setField(member2, "id", 2L);

        ConcertReview concertReview = TestDataUtil.createConcertReviews(concert, member1, 1).get(0);

        // when-then
        assertThatThrownBy(
                () -> concertReview.updateIsActivatedFalse(member2.getId())).isInstanceOf(
                ConcertReviewModifyDeniedException.class);
    }
}
