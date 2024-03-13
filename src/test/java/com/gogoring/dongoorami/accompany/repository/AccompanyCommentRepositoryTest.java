package com.gogoring.dongoorami.accompany.repository;

import static com.gogoring.dongoorami.accompany.AccompanyDataFactory.createAccompanyComment;
import static com.gogoring.dongoorami.accompany.AccompanyDataFactory.createAccompanyPosts;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyCommentRequest;
import com.gogoring.dongoorami.global.config.QueryDslConfig;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import java.util.ArrayList;
import java.util.Arrays;
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
class AccompanyCommentRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccompanyCommentRepository accompanyCommentRepository;

    @Autowired
    private AccompanyPostRepository accompanyPostRepository;

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
        AccompanyPost accompanyPost = accompanyPostRepository.saveAll(
                createAccompanyPosts(member1, 1)).get(0);
        List<AccompanyComment> accompanyComments = new ArrayList<>();
        accompanyComments.addAll(createAccompanyComment(member2, 3));
        accompanyComments.add(AccompanyCommentRequest.createAccompanyApplyCommentRequest()
                .toEntity(member2, true));
        accompanyComments.add(AccompanyCommentRequest.createAccompanyApplyCommentRequest()
                .toEntity(member3, true));
        accompanyComments.stream().forEach(accompanyPost::addAccompanyComment);
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
        Member member = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        memberRepository.save(member);
        AccompanyPost accompanyPost = accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 1)).get(0);
        List<AccompanyComment> accompanyComments = new ArrayList<>();
        accompanyComments.addAll(createAccompanyComment(member, 3));
        accompanyComments.add(AccompanyCommentRequest.createAccompanyApplyCommentRequest()
                .toEntity(member, true));
        accompanyComments.stream().forEach(accompanyPost::addAccompanyComment);
        accompanyCommentRepository.saveAll(accompanyComments);

        // when
        boolean isAccompanyApplied = accompanyCommentRepository.existsByAccompanyPostIdAndMemberIdAndIsAccompanyApplyCommentTrue(
                accompanyPost.getId(),
                member.getId());

        // then
        assertThat(isAccompanyApplied, equalTo(true));
    }

    @Test
    @DisplayName("특정 멤버의 동행 신청 여부를 조회할 수 있다. - 신청하지 않은 경우")
    void success_existsByMemberIdAndIsAccompanyApplyCommentTrue_given_notAppliedMember() {
        // given
        Member member = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        memberRepository.save(member);
        AccompanyPost accompanyPost = accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 1)).get(0);
        List<AccompanyComment> accompanyComments = new ArrayList<>();
        accompanyComments.addAll(createAccompanyComment(member, 3));
        accompanyComments.add(
                new AccompanyCommentRequest("가는 길만 동행해도 괜찮을까요!?").toEntity(member, false));
        accompanyComments.stream().forEach(accompanyPost::addAccompanyComment);
        accompanyCommentRepository.saveAll(accompanyComments);

        // when
        boolean isAccompanyApplied = accompanyCommentRepository.existsByAccompanyPostIdAndMemberIdAndIsAccompanyApplyCommentTrue(
                accompanyPost.getId(),
                member.getId());

        // then
        assertThat(isAccompanyApplied, equalTo(false));
    }
}