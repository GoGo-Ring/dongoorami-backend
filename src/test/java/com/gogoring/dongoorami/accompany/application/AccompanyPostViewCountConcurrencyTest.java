package com.gogoring.dongoorami.accompany.application;

import static com.gogoring.dongoorami.accompany.AccompanyDataFactory.createAccompanyPosts;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.repository.AccompanyPostRepository;
import com.gogoring.dongoorami.concert.ConcertDataFactory;
import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.repository.ConcertRepository;
import com.gogoring.dongoorami.member.MemberDataFactory;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AccompanyPostViewCountConcurrencyTest {

    @Autowired
    private AccompanyService accompanyService;

    @Autowired
    private AccompanyPostRepository accompanyPostRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        accompanyPostRepository.deleteAll();
        concertRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        accompanyPostRepository.deleteAll();
        concertRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("동시에 여러 조회가 이루어지는 경우, 모든 조회수가 정상적으로 반영되지 않는다.")
    void fail_updateViewCount() {
        // given
        Member member = MemberDataFactory.createMember();
        memberRepository.save(member);
        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());
        int size = 1, requestCnt = 100;
        AccompanyPost accompanyPost = accompanyPostRepository.saveAll(
                createAccompanyPosts(member, size, concert)).get(0);
        Long viewCountBeforeRequests = accompanyPost.getViewCount(), viewCountAfterRequests;

        // when
        List<CompletableFuture<Void>> getAccompanyPostRequestFutures = IntStream.range(0, 100)
                .mapToObj(i -> CompletableFuture.runAsync(() ->
                        accompanyService.getAccompanyPost(member.getId(), accompanyPost.getId())
                ))
                .toList();
        CompletableFuture.allOf(
                getAccompanyPostRequestFutures.toArray(
                        new CompletableFuture[getAccompanyPostRequestFutures.size()])
        ).join();
        viewCountAfterRequests = accompanyPostRepository.findById(accompanyPost.getId()).get()
                .getViewCount();

        // then
        System.out.println("viewCountBeforeRequests: " + viewCountBeforeRequests);
        System.out.println("viewCountAfterRequests: " + viewCountAfterRequests);
        Assertions.assertThat(viewCountAfterRequests - viewCountBeforeRequests)
                .isNotEqualTo(requestCnt);
    }

    @Test
    @DisplayName("동시에 여러 조회가 이루어지는 경우, 모든 조회수가 정상적으로 반영된다. - 조회수 증가 시 update 문 사용")
    void success_updateViewCount_given_update_query() {
        // given
        Member member = MemberDataFactory.createMember();
        memberRepository.save(member);
        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());
        int size = 1, requestCnt = 100;
        AccompanyPost accompanyPost = accompanyPostRepository.saveAll(
                createAccompanyPosts(member, size, concert)).get(0);
        Long viewCountBeforeRequests = accompanyPost.getViewCount(), viewCountAfterRequests;

        // when
        long beforeTime = System.currentTimeMillis();
        List<CompletableFuture<Void>> getAccompanyPostRequestFutures = IntStream.range(0, 100)
                .mapToObj(i -> CompletableFuture.runAsync(() ->
                        accompanyService.getAccompanyPostWithViewCountUpdateQuery(member.getId(),
                                accompanyPost.getId())
                ))
                .toList();
        CompletableFuture.allOf(
                getAccompanyPostRequestFutures.toArray(
                        new CompletableFuture[getAccompanyPostRequestFutures.size()])
        ).join();
        viewCountAfterRequests = accompanyPostRepository.findById(accompanyPost.getId()).get()
                .getViewCount();
        long afterTime = System.currentTimeMillis();

        // then
        System.out.println("소요시간(ms): " + (afterTime - beforeTime));
        Assertions.assertThat(viewCountAfterRequests - viewCountBeforeRequests)
                .isEqualTo(requestCnt);
    }

}