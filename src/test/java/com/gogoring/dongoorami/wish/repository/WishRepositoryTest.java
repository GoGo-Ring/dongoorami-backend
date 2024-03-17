package com.gogoring.dongoorami.wish.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.gogoring.dongoorami.accompany.AccompanyDataFactory;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.repository.AccompanyPostRepository;
import com.gogoring.dongoorami.concert.ConcertDataFactory;
import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.repository.ConcertRepository;
import com.gogoring.dongoorami.global.config.QueryDslConfig;
import com.gogoring.dongoorami.member.MemberDataFactory;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import com.gogoring.dongoorami.wish.WishDataFactory;
import com.gogoring.dongoorami.wish.domain.Wish;
import com.gogoring.dongoorami.wish.exception.WishErrorCode;
import com.gogoring.dongoorami.wish.exception.WishNotFoundException;
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

@Import(QueryDslConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class WishRepositoryTest {

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private AccompanyPostRepository accompanyPostRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        wishRepository.deleteAll();
        accompanyPostRepository.deleteAll();
        concertRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        wishRepository.deleteAll();
        accompanyPostRepository.deleteAll();
        concertRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("특정 동행 구인글에 대한 특정 회원의 찜 정보 존재 여부를 확인할 수 있다.")
    void success_existsByAccompanyPostAndMember() {
        // given
        Member member1 = MemberDataFactory.createMember();
        Member member2 = MemberDataFactory.createMember();
        memberRepository.saveAll(List.of(member1, member2));

        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        AccompanyPost accompanyPost = AccompanyDataFactory.createAccompanyPosts(member1, 1, concert)
                .get(0);
        accompanyPostRepository.save(accompanyPost);

        Wish wish = WishDataFactory.createWish(member1, accompanyPost);
        wishRepository.save(wish);

        // when
        Boolean isExistOfMember1 = wishRepository.existsByAccompanyPostAndMember(accompanyPost,
                member1);
        Boolean isExistOfMember2 = wishRepository.existsByAccompanyPostAndMember(accompanyPost,
                member2);

        // then
        assertThat(isExistOfMember1).isTrue();
        assertThat(isExistOfMember2).isFalse();
    }

    @Test
    @DisplayName("특정 동행 구인글에 대한 특정 회원의 찜 정보를 조회할 수 있다.")
    void success_findByAccompanyPostAndMember() {
        // given
        Member member = MemberDataFactory.createMember();
        memberRepository.save(member);

        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        AccompanyPost accompanyPost = AccompanyDataFactory.createAccompanyPosts(member, 1, concert)
                .get(0);
        accompanyPostRepository.save(accompanyPost);

        Wish wish = WishDataFactory.createWish(member, accompanyPost);
        wishRepository.save(wish);

        // when
        Wish savedWish = wishRepository.findByAccompanyPostAndMember(accompanyPost, member)
                .orElseThrow(() -> new WishNotFoundException(WishErrorCode.WISH_NOT_FOUND));

        // then
        assertThat(savedWish.getId()).isEqualTo(wish.getId());
    }
}
