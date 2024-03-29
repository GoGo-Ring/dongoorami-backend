package com.gogoring.dongoorami.accompany.repository;

import static com.gogoring.dongoorami.accompany.AccompanyDataFactory.createAccompanyComment;
import static com.gogoring.dongoorami.accompany.AccompanyDataFactory.createAccompanyPosts;
import static com.gogoring.dongoorami.accompany.AccompanyDataFactory.createAccompanyReviews;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.gogoring.dongoorami.accompany.AccompanyDataFactory;
import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.domain.AccompanyReview;
import com.gogoring.dongoorami.accompany.domain.AccompanyReview.AccompanyReviewStatusType;
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
import java.util.Set;
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
import org.springframework.test.util.ReflectionTestUtils;

@Import(QueryDslConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class AccompanyReviewRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccompanyCommentRepository accompanyCommentRepository;

    @Autowired
    private AccompanyPostRepository accompanyPostRepository;

    @Autowired
    private ConcertRepository concertRepository;
    @Autowired
    private AccompanyReviewRepository accompanyReviewRepository;

    @BeforeEach
    void setUp() {
        accompanyReviewRepository.deleteAll();
        accompanyCommentRepository.deleteAll();
        accompanyPostRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        accompanyReviewRepository.deleteAll();
        accompanyCommentRepository.deleteAll();
        accompanyPostRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("현재까지 특정 동행 구인글에 대해 동행을 확정한 회원 목록을 조회할 수 있다.")
    void success_findDistinctReviewerAndRevieweeByAccompanyPostId() {
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
        List<AccompanyReview> accompanyReviews = new ArrayList<>();
        accompanyReviews.add(AccompanyReview.builder()
                .reviewer(member1)
                .reviewee(member2)
                .accompanyPost(accompanyPost)
                .build());
        accompanyReviews.add(AccompanyReview.builder()
                .reviewer(member2)
                .reviewee(member1)
                .accompanyPost(accompanyPost)
                .build());
        accompanyReviewRepository.saveAll(accompanyReviews);

        // when
        Set<Long> companionIds = accompanyReviewRepository.findDistinctReviewerAndRevieweeByAccompanyPostId(
                accompanyPost.getId());

        // then
        assertThat(companionIds.size(), equalTo(2));
    }

    @Test
    @DisplayName("특정 동행 구인글에 대해 동행자 A, 동행자 B 간의 동행 리뷰가 생성되어 있는지 확인할 수 있다.")
    void success_existsByCompanionsAndAccompanyPostId() {
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
        memberRepository.saveAll(Arrays.asList(member1, member2));
        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());
        AccompanyPost accompanyPost = accompanyPostRepository.saveAll(
                createAccompanyPosts(member1, 1, concert)).get(0);
        List<AccompanyComment> accompanyComments = new ArrayList<>();
        accompanyComments.addAll(createAccompanyComment(accompanyPost, member1, 3));
        accompanyComments.add(AccompanyCommentRequest.createAccompanyApplyCommentRequest()
                .toEntity(accompanyPost, member2, true));
        accompanyCommentRepository.saveAll(accompanyComments);
        List<AccompanyReview> accompanyReviews = new ArrayList<>();
        accompanyReviews.add(AccompanyReview.builder()
                .reviewer(member1)
                .reviewee(member2)
                .accompanyPost(accompanyPost)
                .build());
        accompanyReviews.add(AccompanyReview.builder()
                .reviewer(member2)
                .reviewee(member1)
                .accompanyPost(accompanyPost)
                .build());
        accompanyReviewRepository.saveAll(accompanyReviews);

        // when
        boolean isMember1Member2AccompanyReviewExist = accompanyReviewRepository.existsByCompanionsAndAccompanyPostId(
                member1.getId(),
                member2.getId(), accompanyPost.getId());
        boolean isMember2Member1AccompanyReviewExist = accompanyReviewRepository.existsByCompanionsAndAccompanyPostId(
                member2.getId(),
                member1.getId(), accompanyPost.getId());
        boolean isMember1Member3AccompanyReviewExist = accompanyReviewRepository.existsByCompanionsAndAccompanyPostId(
                member1.getId(),
                member3.getId(), accompanyPost.getId());

        // then
        assertThat(isMember1Member2AccompanyReviewExist, equalTo(true));
        assertThat(isMember2Member1AccompanyReviewExist, equalTo(true));
        assertThat(isMember1Member3AccompanyReviewExist, equalTo(false));
    }

    @Test
    @DisplayName("리뷰 작성자 id, 리뷰 대상자 id, 동행구인글 id로 동행 리뷰를 조회할 수 있다.")
    void success_findAccompanyReviewByReviewerIdAndRevieweeIdAndAccompanyPostId() {
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
        List<AccompanyReview> accompanyReviews = new ArrayList<>();
        accompanyReviews.add(AccompanyReview.builder()
                .reviewer(member1)
                .reviewee(member2)
                .accompanyPost(accompanyPost)
                .build());
        accompanyReviews.add(AccompanyReview.builder()
                .reviewer(member2)
                .reviewee(member1)
                .accompanyPost(accompanyPost)
                .build());
        accompanyReviewRepository.saveAll(accompanyReviews);

        // when
        AccompanyReview accompanyReview = accompanyReviewRepository.findAccompanyReviewByReviewerIdAndRevieweeIdAndAccompanyPostId(
                member1.getId(), member2.getId(), accompanyPost.getId());

        // then
        assertThat(accompanyReview, notNullValue());
    }

    @Test
    @DisplayName("특정 멤버가 받은 별점 평균을 조회할 수 있다.")
    void success_averageRatingByRevieweeId() {
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
        Member member4 = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        memberRepository.saveAll(Arrays.asList(member1, member2, member3, member4));
        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());
        AccompanyPost accompanyPost = accompanyPostRepository.saveAll(
                createAccompanyPosts(member1, 1, concert)).get(0);
        List<AccompanyComment> accompanyComments = new ArrayList<>();
        accompanyComments.addAll(createAccompanyComment(accompanyPost, member1, 3));
        accompanyComments.add(AccompanyCommentRequest.createAccompanyApplyCommentRequest()
                .toEntity(accompanyPost, member2, true));
        accompanyCommentRepository.saveAll(accompanyComments);
        AccompanyReview accompanyReview1 = AccompanyReview.builder()
                .reviewer(member2)
                .reviewee(member1)
                .accompanyPost(accompanyPost)
                .build();
        AccompanyReview accompanyReview2 = AccompanyReview.builder()
                .reviewer(member3)
                .reviewee(member1)
                .accompanyPost(accompanyPost)
                .build();
        AccompanyReview accompanyReview3 = AccompanyReview.builder()
                .reviewer(member4)
                .reviewee(member1)
                .accompanyPost(accompanyPost)
                .build();
        ReflectionTestUtils.setField(accompanyReview1, "rating", 5);
        ReflectionTestUtils.setField(accompanyReview2, "rating", 4);
        ReflectionTestUtils.setField(accompanyReview3, "rating", 3);
        accompanyReviewRepository.saveAll(
                Arrays.asList(accompanyReview1, accompanyReview2, accompanyReview3));

        // when
        Integer ratingAveragePercent = accompanyReviewRepository.averageRatingPercentByRevieweeId(
                member1.getId());

        // then
        assertThat(ratingAveragePercent, equalTo((5 + 4 + 3) / 3 * 20));
    }

    @Test
    @DisplayName("특정 공연에 대한 동행 목록을 조회할 수 있다.")
    void success_findAllByConcertId() {
        // given
        Member member1 = MemberDataFactory.createMember();
        Member member2 = MemberDataFactory.createMember();
        Member member3 = MemberDataFactory.createMember();
        List<Member> members = Arrays.asList(member1, member2, member3);
        memberRepository.saveAll(members);
        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());
        AccompanyPost accompanyPost = accompanyPostRepository.saveAll(
                createAccompanyPosts(member1, 1, concert)).get(0);
        int size = accompanyReviewRepository.saveAll(
                createAccompanyReviews(accompanyPost, members)).size();

        // when
        List<AccompanyReview> accompanyReviews = accompanyReviewRepository.findAllByConcertIdAndActivatedConcertAndActivatedAccompanyReviewAndProceedingStatus(
                concert.getId());

        // then
        assertThat(accompanyReviews.size(), equalTo(size));
    }

    @Test
    @DisplayName("id 내림차순으로 특정 회원이 받은 후기 목록을 조회할 수 있다.")
    void success_findAllByRevieweeAndStatus() {
        // given
        Member member1 = MemberDataFactory.createMember();
        Member member2 = MemberDataFactory.createMember();
        Member member3 = MemberDataFactory.createMember();
        memberRepository.saveAll(List.of(member1, member2, member3));

        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());

        AccompanyPost accompanyPost = accompanyPostRepository.save(
                createAccompanyPosts(member1, 1, concert).get(0));

        List<AccompanyComment> accompanyComments = new ArrayList<>(
                createAccompanyComment(accompanyPost, member1, 3));
        accompanyComments.add(AccompanyCommentRequest.createAccompanyApplyCommentRequest()
                .toEntity(accompanyPost, member2, true));
        accompanyCommentRepository.saveAll(accompanyComments);

        AccompanyReview accompanyReview1 = AccompanyDataFactory.createAccompanyReview(accompanyPost,
                member2, member1);
        AccompanyReview accompanyReview2 = AccompanyDataFactory.createAccompanyReview(accompanyPost,
                member3, member1);
        ReflectionTestUtils.setField(accompanyReview1, "rating", 5);
        ReflectionTestUtils.setField(accompanyReview2, "rating", 4);
        ReflectionTestUtils.setField(accompanyReview1, "status",
                AccompanyReviewStatusType.AFTER_ACCOMPANY_AND_WRITTEN);
        ReflectionTestUtils.setField(accompanyReview2, "status",
                AccompanyReviewStatusType.AFTER_ACCOMPANY_AND_WRITTEN);
        accompanyReviewRepository.saveAll(List.of(accompanyReview1, accompanyReview2));

        long maxId = Math.max(accompanyReview1.getId(), accompanyReview2.getId());

        // when
        int size = 2;
        Slice<AccompanyReview> accompanyReviews = accompanyReviewRepository.findAllByMemberAndStatus(
                maxId + 1, size, member1, false,
                AccompanyReviewStatusType.AFTER_ACCOMPANY_AND_WRITTEN);

        // then
        Assertions.assertThat(accompanyReviews.getSize()).isEqualTo(size);
    }

    @Test
    @DisplayName("id 내림차순으로 특정 회원이 작성 전인 후기 목록을 조회할 수 있다.")
    void success_findAllByReviewerAndStatus() {
        // given
        Member member1 = MemberDataFactory.createMember();
        Member member2 = MemberDataFactory.createMember();
        Member member3 = MemberDataFactory.createMember();
        memberRepository.saveAll(List.of(member1, member2, member3));

        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());

        AccompanyPost accompanyPost = accompanyPostRepository.save(
                createAccompanyPosts(member1, 1, concert).get(0));

        List<AccompanyComment> accompanyComments = new ArrayList<>(
                createAccompanyComment(accompanyPost, member1, 3));
        accompanyComments.add(AccompanyCommentRequest.createAccompanyApplyCommentRequest()
                .toEntity(accompanyPost, member2, true));
        accompanyCommentRepository.saveAll(accompanyComments);

        AccompanyReview accompanyReview1 = AccompanyDataFactory.createAccompanyReview(accompanyPost,
                member1, member2);
        AccompanyReview accompanyReview2 = AccompanyDataFactory.createAccompanyReview(accompanyPost,
                member1, member3);
        ReflectionTestUtils.setField(accompanyReview1, "status",
                AccompanyReviewStatusType.AFTER_ACCOMPANY_AND_NOT_WRITTEN);
        ReflectionTestUtils.setField(accompanyReview2, "status",
                AccompanyReviewStatusType.AFTER_ACCOMPANY_AND_NOT_WRITTEN);
        accompanyReviewRepository.saveAll(List.of(accompanyReview1, accompanyReview2));

        long maxId = Math.max(accompanyReview1.getId(), accompanyReview2.getId());

        // when
        int size = 2;
        Slice<AccompanyReview> accompanyReviews = accompanyReviewRepository.findAllByMemberAndStatus(
                maxId + 1, size, member1, true,
                AccompanyReviewStatusType.AFTER_ACCOMPANY_AND_NOT_WRITTEN);

        // then
        Assertions.assertThat(accompanyReviews.getSize()).isEqualTo(size);
    }

    @Test
    @DisplayName("특정 회원이 작성한 동행 후기 목록을 조회할 수 있다.")
    void success_findAllByReviewerAndStatusAndIsActivatedIsTrue() {
        // given
        Member member1 = MemberDataFactory.createMember();
        Member member2 = MemberDataFactory.createMember();
        Member member3 = MemberDataFactory.createMember();
        memberRepository.saveAll(List.of(member1, member2, member3));

        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());

        AccompanyPost accompanyPost = accompanyPostRepository.save(
                createAccompanyPosts(member1, 1, concert).get(0));

        List<AccompanyComment> accompanyComments = new ArrayList<>(
                createAccompanyComment(accompanyPost, member1, 3));
        accompanyComments.add(AccompanyCommentRequest.createAccompanyApplyCommentRequest()
                .toEntity(accompanyPost, member2, true));
        accompanyCommentRepository.saveAll(accompanyComments);

        AccompanyReview accompanyReview1 = AccompanyDataFactory.createAccompanyReview(accompanyPost,
                member1, member2);
        AccompanyReview accompanyReview2 = AccompanyDataFactory.createAccompanyReview(accompanyPost,
                member1, member3);
        ReflectionTestUtils.setField(accompanyReview1, "status",
                AccompanyReviewStatusType.AFTER_ACCOMPANY_AND_WRITTEN);
        ReflectionTestUtils.setField(accompanyReview2, "status",
                AccompanyReviewStatusType.AFTER_ACCOMPANY_AND_WRITTEN);
        accompanyReviewRepository.saveAll(List.of(accompanyReview1, accompanyReview2));

        // when
        List<AccompanyReview> accompanyReviews = accompanyReviewRepository.findAllByReviewerAndStatusAndIsActivatedIsTrue(
                member1, AccompanyReviewStatusType.AFTER_ACCOMPANY_AND_WRITTEN);

        // then
        Assertions.assertThat(accompanyReviews.size()).isEqualTo(2);
        Assertions.assertThat(accompanyReviews).contains(accompanyReview1, accompanyReview2);
    }
}