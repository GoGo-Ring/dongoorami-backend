package com.gogoring.dongoorami.concert.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.gogoring.dongoorami.concert.ConcertDataFactory;
import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.domain.ConcertReview;
import com.gogoring.dongoorami.concert.exception.ConcertErrorCode;
import com.gogoring.dongoorami.concert.exception.ConcertReviewNotFoundException;
import com.gogoring.dongoorami.global.config.QueryDslConfig;
import com.gogoring.dongoorami.member.MemberDataFactory;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

@Import(QueryDslConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ConcertReviewRepositoryTest {

    @Autowired
    private ConcertReviewRepository concertReviewRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        concertReviewRepository.deleteAll();
        concertRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        concertReviewRepository.deleteAll();
        concertRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("id 내림차순으로 특정 공연의 후기를 페이징 조회할 수 있다.")
    void success_findAllByConcertAndIsActivatedIsTrueOrderByIdDesc() {
        // given
        Member member = MemberDataFactory.createMember();
        memberRepository.save(member);

        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        int size = 10;
        List<ConcertReview> concertReviews = ConcertDataFactory.createConcertReviews(concert,
                member,
                size + 5);
        concertReviewRepository.saveAll(concertReviews);

        long minId = 987654321L;
        for (ConcertReview concertReview : concertReviews) {
            minId = Math.min(minId, concertReview.getId());
        }

        // when
        Slice<ConcertReview> slice = concertReviewRepository.findAllByConcertAndIsActivatedIsTrueOrderByIdDesc(
                concert, PageRequest.of(0, size));

        // then
        assertThat(slice.getSize()).isEqualTo(size);
        assertThat(slice.getContent().stream().map(ConcertReview::getId)
                .toList()).doesNotContain(minId);
    }

    @Test
    @DisplayName("id 내림차순으로 특정 id 값 이하의 특정 공연의 후기를 페이징 조회할 수 있다.")
    void success_findAllByIdLessThanAndConcertAndIsActivatedIsTrueOrderByIdDesc() {
        // given
        Member member = MemberDataFactory.createMember();
        memberRepository.save(member);

        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        int size = 10;
        List<ConcertReview> concertReviews = ConcertDataFactory.createConcertReviews(concert,
                member,
                size + 5);
        concertReviewRepository.saveAll(concertReviews);

        long minId = 987654321L;
        long maxId = -1L;
        for (ConcertReview concertReview : concertReviews) {
            minId = Math.min(minId, concertReview.getId());
            maxId = Math.max(maxId, concertReview.getId());
        }

        // when
        Slice<ConcertReview> slice = concertReviewRepository.findAllByIdLessThanAndConcertAndIsActivatedIsTrueOrderByIdDesc(
                maxId, concert, PageRequest.of(0, size));

        // then
        assertThat(slice.getSize()).isEqualTo(size);
        assertThat(slice.getContent().stream().map(ConcertReview::getId)
                .toList()).doesNotContain(minId, maxId);
    }

    @Test
    @DisplayName("id로 공연 후기를 조회할 수 있다.")
    void success_findByIdAndIsActivatedIsTrue() {
        // given
        Member member = MemberDataFactory.createMember();
        memberRepository.save(member);

        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        ConcertReview concertReview = ConcertDataFactory.createConcertReviews(concert, member, 1)
                .get(0);
        concertReviewRepository.save(concertReview);

        // when
        ConcertReview savedConcertReview = concertReviewRepository.findByIdAndIsActivatedIsTrue(
                concertReview.getId()).orElseThrow(() -> new ConcertReviewNotFoundException(
                ConcertErrorCode.CONCERT_REVIEW_NOT_FOUND));

        // then
        assertThat(savedConcertReview.getId()).isEqualTo(concertReview.getId());
    }
}
