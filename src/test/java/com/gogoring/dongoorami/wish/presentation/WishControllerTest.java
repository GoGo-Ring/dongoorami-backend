package com.gogoring.dongoorami.wish.presentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogoring.dongoorami.accompany.AccompanyDataFactory;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.repository.AccompanyPostRepository;
import com.gogoring.dongoorami.concert.ConcertDataFactory;
import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.repository.ConcertRepository;
import com.gogoring.dongoorami.global.customMockUser.WithCustomMockUser;
import com.gogoring.dongoorami.global.jwt.TokenProvider;
import com.gogoring.dongoorami.member.MemberDataFactory;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import com.gogoring.dongoorami.wish.WishDataFactory;
import com.gogoring.dongoorami.wish.domain.Wish;
import com.gogoring.dongoorami.wish.repository.WishRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
public class WishControllerTest {

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private AccompanyPostRepository accompanyPostRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithCustomMockUser
    @DisplayName("동행 구인글에 대해 찜을 할 수 있다.")
    void success_createWish() throws Exception {
        // given
        Member member = MemberDataFactory.createLoginMember();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());

        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        AccompanyPost accompanyPost = AccompanyDataFactory.createAccompanyPosts(member, 1, concert)
                .get(0);
        accompanyPostRepository.save(accompanyPost);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/wishes/{accompanyPostId}", accompanyPost.getId()).header(
                                "Authorization", accessToken));

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(document("{ClassName}/createWish",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("accompanyPostId").description("찜할 동행 구인글 아이디")
                        ))
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("이전에 찜했다가 취소한 동행 구인글에 대해 다시 찜을 할 수 있다.")
    void success_createWish_given_wish() throws Exception {
        // given
        Member member = MemberDataFactory.createLoginMember();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());

        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        AccompanyPost accompanyPost = AccompanyDataFactory.createAccompanyPosts(member, 1, concert)
                .get(0);
        accompanyPostRepository.save(accompanyPost);

        Wish wish = WishDataFactory.createWish(member, accompanyPost);
        wish.updateIsActivatedFalse();
        wishRepository.save(wish);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/wishes/{accompanyPostId}", accompanyPost.getId()).header(
                        "Authorization", accessToken));

        // then
        resultActions.andExpect(status().isCreated());
    }
}
