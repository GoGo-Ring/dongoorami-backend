package com.gogoring.dongoorami.accompany.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.lessThan;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost.AccompanyPurposeType;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostFilterRequest;
import com.gogoring.dongoorami.global.config.QueryDslConfig;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import java.time.LocalDate;
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
                .name("김뫄뫄")
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        memberRepository.save(member);
        accompanyPostRepository.saveAll(createAccompanyPosts(member, 30));
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
                .name("김뫄뫄")
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        memberRepository.save(member);
        accompanyPostRepository.saveAll(createAccompanyPosts(member, 30));
        Long cursorId = 1000000L;
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
                .name("김뫄뫄")
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
                .purposes(Arrays.asList("관람", "숙박", "이동"))
                .build();
        AccompanyPostFilterRequest accompanyPostFilterRequest2 = AccompanyPostFilterRequest.builder()
                .gender("여")
                .region("경상북도/경상남도")
                .startAge(13L)
                .endAge(17L)
                .totalPeople(1L)
                .concertPlace("KSPO DOME")
                .purposes(Arrays.asList("관람", "숙박"))
                .build();
        accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 30, accompanyPostFilterRequest1));
        accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 30, accompanyPostFilterRequest2));
        Long cursorId = 1000000L;
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
                .name("김뫄뫄")
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
        accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 3, accompanyPostFilterRequest1));
        accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 3, accompanyPostFilterRequest2));
        Long cursorId = 1000000L;
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
                .name("김뫄뫄")
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
        accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 3, accompanyPostFilterRequest1));
        accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 3, accompanyPostFilterRequest2));
        Long cursorId = 1000000L;
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

    private List<AccompanyPost> createAccompanyPosts(Member member, int size) {
        List<AccompanyPost> accompanyPosts = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            accompanyPosts.add(AccompanyPost.builder()
                    .member(member)
                    .concertName("2024 SG워너비 콘서트 : 우리의 노래")
                    .concertPlace("KSPO DOME")
                    .startDate(LocalDate.of(2024, 3, 22))
                    .endDate(LocalDate.of(2024, 3, 22))
                    .title("서울 같이 갈 울싼 사람 구합니다~~")
                    .gender("여")
                    .region("수도권(경기, 인천 포함)")
                    .content("같이 올라갈 사람 구해요~")
                    .startAge(23L)
                    .endAge(37L)
                    .totalPeople(2L)
                    .purposes(Arrays.asList(AccompanyPurposeType.ACCOMMODATION,
                            AccompanyPurposeType.TRANSPORTATION)).build());
        }

        return accompanyPosts;
    }

    private List<AccompanyPost> createAccompanyPosts(Member member, int size,
            AccompanyPostFilterRequest accompanyPostFilterRequest) {
        List<AccompanyPost> accompanyPosts = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            accompanyPosts.add(AccompanyPost.builder()
                    .member(member)
                    .concertName("2024 SG워너비 콘서트 : 우리의 노래")
                    .concertPlace(accompanyPostFilterRequest.getConcertPlace())
                    .startDate(LocalDate.of(2024, 3, 22))
                    .endDate(LocalDate.of(2024, 3, 22))
                    .title("서울 같이 갈 울싼 사람 구합니다~~")
                    .gender(accompanyPostFilterRequest.getGender())
                    .region(accompanyPostFilterRequest.getRegion())
                    .content("같이 올라갈 사람 구해요~")
                    .startAge(accompanyPostFilterRequest.getStartAge())
                    .endAge(accompanyPostFilterRequest.getEndAge())
                    .totalPeople(accompanyPostFilterRequest.getTotalPeople())
                    .purposes(accompanyPostFilterRequest.getPurposes().stream().map(
                            AccompanyPurposeType::getValue).toList()).build());
        }

        return accompanyPosts;
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
                                        || accompanyPost.getConcertPlace()
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