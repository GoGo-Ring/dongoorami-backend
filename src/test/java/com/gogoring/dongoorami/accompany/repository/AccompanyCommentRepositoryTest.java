package com.gogoring.dongoorami.accompany.repository;

import static com.gogoring.dongoorami.accompany.AccompanyDataFactory.createAccompanyComment;
import static com.gogoring.dongoorami.accompany.AccompanyDataFactory.createAccompanyPosts;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyCommentRequest;
import com.gogoring.dongoorami.concert.ConcertDataFactory;
import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.repository.ConcertRepository;
import com.gogoring.dongoorami.global.config.QueryDslConfig;
import com.gogoring.dongoorami.member.MemberDataFactory;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;

@Import(QueryDslConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class AccompanyCommentRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccompanyCommentRepository accompanyCommentRepository;

    @Autowired
    private AccompanyPostRepository accompanyPostRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @BeforeEach
    void setUp() {
        accompanyCommentRepository.deleteAll();
        accompanyPostRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        accompanyCommentRepository.deleteAll();
        accompanyPostRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("특정 동행 구인글의 댓글 수를 조회할 수 있다.")
    void success_countByAccompanyPostId() {
        // given
        Member member1 = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        memberRepository.save(member1);
        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());
        AccompanyPost accompanyPost = accompanyPostRepository.saveAll(
                createAccompanyPosts(member1, 1, concert)).get(0);
        int size = 3;
        List<AccompanyComment> accompanyComments = new ArrayList<>();
        accompanyComments.addAll(createAccompanyComment(accompanyPost, member1, size));
        accompanyCommentRepository.saveAll(accompanyComments);

        // when
        long commentCount = accompanyCommentRepository.countByAccompanyPostIdAndIsActivatedIsTrue(
                accompanyPost.getId());

        // then
        assertThat(commentCount, equalTo((long) size));
    }

    @Test
    @DisplayName("동행 댓글 신청 수를 조회할 수 있다.")
    void success_countByIsActivatedIsTrueAndIsAccompanyApplyCommentTrue() {
        // given
        Member member1 = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        Member member2 = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        Member member3 = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        memberRepository.saveAll(Arrays.asList(member1, member2, member3));
        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());
        AccompanyPost accompanyPost = accompanyPostRepository.saveAll(
                createAccompanyPosts(member1, 1, concert)).get(0);
        List<AccompanyComment> accompanyComments = new ArrayList<>();
        accompanyComments.addAll(createAccompanyComment(accompanyPost, member1, 3));
        accompanyComments.add(AccompanyCommentRequest.createAccompanyApplyCommentRequest()
                .toEntity(accompanyPost, member2, true));
        accompanyComments.add(AccompanyCommentRequest.createAccompanyApplyCommentRequest()
                .toEntity(accompanyPost, member3, true));
        accompanyCommentRepository.saveAll(accompanyComments);

        // when
        Long waitingCount = accompanyCommentRepository.countByAccompanyPostIdAndIsActivatedIsTrueAndIsAccompanyApplyCommentTrue(
                accompanyPost.getId());

        // then
        assertThat(waitingCount, equalTo(2L));
    }

    @Test
    @DisplayName("특정 멤버의 동행 신청 여부를 조회할 수 있다. - 신청한 경우")
    void success_existsByMemberIdAndIsAccompanyApplyCommentTrue_given_appliedMember() {
        // given
        Member member1 = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        Member member2 = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        memberRepository.saveAll(Arrays.asList(member1, member2));
        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());
        AccompanyPost accompanyPost = accompanyPostRepository.saveAll(
                createAccompanyPosts(member1, 1, concert)).get(0);
        List<AccompanyComment> accompanyComments = new ArrayList<>();
        accompanyComments.addAll(createAccompanyComment(accompanyPost, member1, 3));
        accompanyComments.add(AccompanyCommentRequest.createAccompanyApplyCommentRequest()
                .toEntity(accompanyPost, member2, true));
        accompanyCommentRepository.saveAll(accompanyComments);

        // when
        boolean isAccompanyApplied = accompanyCommentRepository.existsByAccompanyPostIdAndIsActivatedIsTrueAndMemberIdAndIsAccompanyApplyCommentTrue(
                accompanyPost.getId(),
                member2.getId());

        // then
        assertThat(isAccompanyApplied, equalTo(true));
    }

    @Test
    @DisplayName("특정 멤버의 동행 신청 여부를 조회할 수 있다. - 신청하지 않은 경우")
    void success_existsByMemberIdAndIsAccompanyApplyCommentTrue_given_notAppliedMember() {
        // given
        Member member1 = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        Member member2 = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        memberRepository.saveAll(Arrays.asList(member1, member2));
        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());
        AccompanyPost accompanyPost = accompanyPostRepository.saveAll(
                createAccompanyPosts(member1, 1, concert)).get(0);
        List<AccompanyComment> accompanyComments = new ArrayList<>();
        accompanyComments.addAll(createAccompanyComment(accompanyPost, member2, 3));
        accompanyComments.add(
                new AccompanyCommentRequest("가는 길만 동행해도 괜찮을까요!?").toEntity(accompanyPost, member2,
                        false));
        accompanyCommentRepository.saveAll(accompanyComments);

        // when
        boolean isAccompanyApplied = accompanyCommentRepository.existsByAccompanyPostIdAndIsActivatedIsTrueAndMemberIdAndIsAccompanyApplyCommentTrue(
                accompanyPost.getId(),
                member2.getId());

        // then
        assertThat(isAccompanyApplied, equalTo(false));
    }

    @Test
    @DisplayName("id 내림차순으로 특정 멤버가 작성한 동행 구인 댓글 목록을 조회할 수 있다.")
    void success_findAllByMember() {
        // given
        Member member = MemberDataFactory.createMember();
        memberRepository.save(member);

        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        AccompanyPost accompanyPost = createAccompanyPosts(member, 1, concert).get(0);
        accompanyPostRepository.save(accompanyPost);

        int size = 3;
        List<AccompanyComment> accompanyComments = createAccompanyComment(accompanyPost, member,
                size + 1);
        accompanyCommentRepository.saveAll(accompanyComments);

        long maxId = -1L;
        for (AccompanyComment accompanyComment : accompanyComments) {
            maxId = Math.max(maxId, accompanyComment.getId());
        }

        // when
        Slice<AccompanyComment> slice = accompanyCommentRepository.findAllByMember(maxId, size,
                member);

        // then
        Assertions.assertThat(slice.getSize()).isEqualTo(size);
        Assertions.assertThat(slice.getContent().stream().map(AccompanyComment::getId)
                .toList()).doesNotContain(maxId);
    }
}