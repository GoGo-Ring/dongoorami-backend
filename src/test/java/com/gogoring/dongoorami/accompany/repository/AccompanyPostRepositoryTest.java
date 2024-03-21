package com.gogoring.dongoorami.accompany.repository;

import static com.gogoring.dongoorami.accompany.AccompanyDataFactory.createAccompanyPosts;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.lessThan;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost.AccompanyPurposeType;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostFilterRequest;
import com.gogoring.dongoorami.concert.ConcertDataFactory;
import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.repository.ConcertRepository;
import com.gogoring.dongoorami.global.config.QueryDslConfig;
import com.gogoring.dongoorami.member.MemberDataFactory;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.repository.MemberRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

@Import(QueryDslConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class AccompanyPostRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccompanyPostRepository accompanyPostRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @BeforeEach
    void setUp() {
        accompanyPostRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        accompanyPostRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("가장 최근에 생성된 특정 개수의 동행 구인 게시글을 조회할 수 있다.")
    void success_findAllByOrderByIdDesc() {
        // given
        Member member = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        memberRepository.save(member);
        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());
        accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 30, concert));
        int size = 10;

        // when
        Slice<AccompanyPost> accompanyPostSlice = accompanyPostRepository.findAllByOrderByIdDesc(
                PageRequest.of(0, size));

        // then
        assertThat(accompanyPostSlice.getContent().size(), equalTo(size));
    }

    @Test
    @DisplayName("주어진 게시글 id 이후에 생성된 특정 개수의 동행 구인 게시글을 조회할 수 있다.")
    void success_findByIdLessThanOrderByIdDesc() {
        // given
        Member member = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        memberRepository.save(member);
        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());
        accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 30, concert));
        Long cursorId = 1000000000L;
        int size = 10;

        // when
        Slice<AccompanyPost> accompanyPostSlice = accompanyPostRepository.findByIdLessThanOrderByIdDesc(
                cursorId, PageRequest.of(0, size));

        // then
        assertThat(accompanyPostSlice.getContent().size(), equalTo(size));
        assertThat(
                accompanyPostSlice.getContent().stream().map(accompanyPost -> accompanyPost.getId())
                        .toList(), everyItem(lessThan(cursorId)));
    }

    @Test
    @DisplayName("주어진 게시글 id 이후에 생성된 특정 개수의 동행 구인 게시글을 검색 필터 기반으로 조회할 수 있다.")
    void success_findByAccompanyPostFilterRequest() {
        // given
        Member member = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        memberRepository.save(member);
        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());
        AccompanyPostFilterRequest accompanyPostFilterRequest1 = AccompanyPostFilterRequest.builder()
                .gender("남")
                .region("수도권(경기, 인천 포함)")
                .startAge(13L)
                .endAge(17L)
                .totalPeople(1L)
                .concertPlace(concert.getPlace())
                .purposes(Arrays.asList("관람", "숙박", "이동"))
                .build();
        AccompanyPostFilterRequest accompanyPostFilterRequest2 = AccompanyPostFilterRequest.builder()
                .gender("여")
                .region("경상북도/경상남도")
                .startAge(13L)
                .endAge(17L)
                .totalPeople(1L)
                .concertPlace(concert.getPlace())
                .purposes(Arrays.asList("관람", "숙박"))
                .build();
        accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 30, accompanyPostFilterRequest1, concert));
        accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 30, accompanyPostFilterRequest2, concert));
        Long cursorId = 1000000000L;
        int size = 10;

        // when
        Slice<AccompanyPost> accompanyPostSlice = accompanyPostRepository.findByAccompanyPostFilterRequest(
                cursorId, size, accompanyPostFilterRequest1);

        // then
        assertThat(accompanyPostSlice.getContent().size(), equalTo(size));
        assertThat(
                accompanyPostSlice.getContent().stream()
                        .map(accompanyPost -> isAccompanyPostEqualsAccompanyPostFilterRequest(
                                accompanyPost, accompanyPostFilterRequest1))
                        .toList(), everyItem(equalTo(true)));
    }

    @Test
    @DisplayName("주어진 게시글 id 이후에 생성된 특정 개수의 동행 구인 게시글을 검색 필터 기반으로 조회할 수 있다. - 동행 목적 테스트")
    void success_findByAccompanyPostFilterRequest_given_purposes() {
        // given
        Member member = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        memberRepository.save(member);
        AccompanyPostFilterRequest accompanyPostFilterRequest1 = AccompanyPostFilterRequest.builder()
                .gender("남")
                .region("수도권(경기, 인천 포함)")
                .startAge(13L)
                .endAge(17L)
                .totalPeople(1L)
                .concertPlace("KSPO DOME")
                .purposes(Arrays.asList("관람", "숙박"))
                .build();
        AccompanyPostFilterRequest accompanyPostFilterRequest2 = AccompanyPostFilterRequest.builder()
                .gender("여")
                .region("경상북도/경상남도")
                .startAge(13L)
                .endAge(17L)
                .totalPeople(1L)
                .concertPlace("KSPO DOME")
                .purposes(List.of("관람"))
                .build();
        AccompanyPostFilterRequest accompanyPostFilterRequest3 = AccompanyPostFilterRequest.builder()
                .purposes(List.of("관람"))
                .build();
        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());
        accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 3, accompanyPostFilterRequest1, concert));
        accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 3, accompanyPostFilterRequest2, concert));
        Long cursorId = 1000000000L;
        int size = 10;

        // when
        Slice<AccompanyPost> accompanyPostSlice = accompanyPostRepository.findByAccompanyPostFilterRequest(
                cursorId, size, accompanyPostFilterRequest3);

        // then
        assertThat(accompanyPostSlice.getContent().size(), equalTo(6));
        assertThat(
                accompanyPostSlice.getContent().stream()
                        .map(accompanyPost -> isAccompanyPostEqualsAccompanyPostFilterRequest(
                                accompanyPost, accompanyPostFilterRequest3))
                        .toList(), everyItem(equalTo(true)));
    }

    @Test
    @DisplayName("주어진 게시글 id 이후에 생성된 특정 개수의 동행 구인 게시글을 검색 필터 기반으로 조회할 수 있다. - 나이 범위 테스트")
    void success_findByAccompanyPostFilterRequest_given_ages() {
        // given
        Member member = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        memberRepository.save(member);
        AccompanyPostFilterRequest accompanyPostFilterRequest1 = AccompanyPostFilterRequest.builder()
                .gender("남")
                .region("수도권(경기, 인천 포함)")
                .startAge(13L)
                .endAge(17L)
                .totalPeople(1L)
                .concertPlace("KSPO DOME")
                .purposes(Arrays.asList("관람", "숙박"))
                .build();
        AccompanyPostFilterRequest accompanyPostFilterRequest2 = AccompanyPostFilterRequest.builder()
                .gender("여")
                .region("경상북도/경상남도")
                .startAge(13L)
                .endAge(17L)
                .totalPeople(1L)
                .concertPlace("KSPO DOME")
                .purposes(List.of("관람"))
                .build();
        AccompanyPostFilterRequest accompanyPostFilterRequest3 = AccompanyPostFilterRequest.builder()
                .startAge(11L)
                .endAge(13L)
                .purposes(List.of("관람"))
                .build();
        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());
        accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 3, accompanyPostFilterRequest1, concert));
        accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 3, accompanyPostFilterRequest2, concert));
        Long cursorId = 1000000000L;
        int size = 10;

        // when
        Slice<AccompanyPost> accompanyPostSlice = accompanyPostRepository.findByAccompanyPostFilterRequest(
                cursorId, size, accompanyPostFilterRequest3);

        // then
        assertThat(accompanyPostSlice.getContent().size(), equalTo(6));
        assertThat(
                accompanyPostSlice.getContent().stream()
                        .map(accompanyPost -> isAccompanyPostEqualsAccompanyPostFilterRequest(
                                accompanyPost, accompanyPostFilterRequest3))
                        .toList(), everyItem(equalTo(true)));
    }

    @Test
    @DisplayName("id 내림차순으로 특정 멤버가 작성한 동행 구인글 목록을 조회할 수 있다.")
    void success_findAllByMember() {
        // given
        Member member = MemberDataFactory.createMember();
        memberRepository.save(member);

        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());

        int size = 3;
        List<AccompanyPost> accompanyPosts = accompanyPostRepository.saveAll(
                createAccompanyPosts(member, size + 1, concert));

        long maxId = -1L;
        for (AccompanyPost accompanyPost : accompanyPosts) {
            maxId = Math.max(maxId, accompanyPost.getId());
        }

        // when
        Slice<AccompanyPost> slice = accompanyPostRepository.findAllByMember(maxId,
                size, member);

        // then
        Assertions.assertThat(slice.getSize()).isEqualTo(size);
        Assertions.assertThat(slice.getContent().stream().map(AccompanyPost::getId)
                .toList()).doesNotContain(maxId);
    }

    @Test
    @DisplayName("키워드 기반 동행 구인글 목록을 조회할 수 있다.")
    void success_findAllByKeyword() {
        // given
        Member member = MemberDataFactory.createMember();
        memberRepository.save(member);

        int size = 3;
        List<Concert> concerts = concertRepository.saveAll(
                ConcertDataFactory.createConcerts(size * 3));
        List<AccompanyPost> accompanyPosts = accompanyPostRepository.saveAll(
                createAccompanyPosts(member, size * 3, concerts.get(0)));
        Long accompanyPostCursorId = accompanyPosts.get(accompanyPosts.size() - 1).getId() + 1;
        String keyword = accompanyPosts.get(0).getTitle()
                .substring(0, accompanyPosts.get(0).getTitle().length() / 2);

        // when
        Slice<AccompanyPost> accompanyPostsContainingKeyword = accompanyPostRepository.findAllByKeyword(
                accompanyPostCursorId, size, keyword);

        // then
        Assertions.assertThat(accompanyPostsContainingKeyword.hasNext()).isEqualTo(true);
        Assertions.assertThat(accompanyPostsContainingKeyword.getContent().size()).isEqualTo(size);
    }

    private boolean isAccompanyPostEqualsAccompanyPostFilterRequest(AccompanyPost accompanyPost,
            AccompanyPostFilterRequest accompanyPostFilterRequest) {
        return ((accompanyPostFilterRequest.getGender() == null || accompanyPost.getGender()
                .equals(accompanyPostFilterRequest.getGender())) &&
                (accompanyPostFilterRequest.getRegion() == null || accompanyPost.getRegion()
                        .getName().equals(accompanyPostFilterRequest.getRegion())) &&
                ((accompanyPostFilterRequest.getStartAge() == null
                        && accompanyPostFilterRequest.getEndAge() == null)
                        ||
                        (accompanyPost.getEndAge() >= accompanyPostFilterRequest.getStartAge() && (
                                accompanyPost.getStartAge()
                                        <= (accompanyPostFilterRequest.getEndAge()))) &&
                                (accompanyPostFilterRequest.getTotalPeople() == null
                                        || accompanyPost.getTotalPeople()
                                        .equals(accompanyPostFilterRequest.getTotalPeople())) &&
                                (accompanyPostFilterRequest.getConcertPlace() == null
                                        || accompanyPost.getConcert().getPlace()
                                        .equals(accompanyPostFilterRequest.getConcertPlace())) &&
                                (accompanyPostFilterRequest.getPurposes() == null
                                        || accompanyPostFilterRequest.getPurposes().isEmpty() ||
                                        accompanyPost.getPurposes().containsAll(
                                                accompanyPostFilterRequest.getPurposes().stream()
                                                        .map(AccompanyPurposeType::getValue)
                                                        .toList())))
        );
    }
}