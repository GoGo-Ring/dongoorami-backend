package com.gogoring.dongoorami.accompany.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.lessThan;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

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
        Long cursorId = 100L;
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
                    .region("서울")
                    .content("같이 올라갈 사람 구해요~")
                    .startAge(23L)
                    .endAge(37L)
                    .totalPeople(2L).build());
        }

        return accompanyPosts;
    }
}