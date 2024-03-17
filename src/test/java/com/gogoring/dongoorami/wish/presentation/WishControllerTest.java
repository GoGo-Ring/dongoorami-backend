package com.gogoring.dongoorami.wish.presentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import java.util.List;
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

    @Test
    @WithCustomMockUser
    @DisplayName("찜 정보를 삭제할 수 있다.")
    void success_deleteWish() throws Exception {
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
        wishRepository.save(wish);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/wishes/{accompanyPostId}", accompanyPost.getId()).header(
                        "Authorization", accessToken));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/deleteWish",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("accompanyPostId").description("찜 삭제할 동행 구인글 아이디")
                        ))
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("찜 목록을 조회할 수 있다. - 최초 요청")
    void success_getWishesFirst() throws Exception {
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

        int size = 3;
        List<Wish> wishes = WishDataFactory.createWishes(member, accompanyPost, size);
        wishRepository.saveAll(wishes);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/wishes").header(
                                "Authorization", accessToken)
                        .param("size", String.valueOf(size))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getWishesFirst",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("size").description(
                                        "조회할 찜 개수, 값 넣지 않으면 기본 10개").optional()
                        ),
                        responseFields(
                                fieldWithPath("hasNext").type(BOOLEAN)
                                        .description("다음 찜 정보 존재 여부"),
                                fieldWithPath("wishGetResponses").type(ARRAY)
                                        .description("찜한 동행 구인글 목록"),
                                fieldWithPath("wishGetResponses[].wishId").type(NUMBER)
                                        .description("찜 아이디"),
                                fieldWithPath("wishGetResponses[].accompanyPostId").type(NUMBER)
                                        .description("찜한 동행 구인글 아이디"),
                                fieldWithPath("wishGetResponses[].title").type(
                                                STRING)
                                        .description("제목"),
                                fieldWithPath("wishGetResponses[].content").type(
                                                STRING)
                                        .description("내용"),
                                fieldWithPath("wishGetResponses[].totalPeople").type(
                                                NUMBER)
                                        .description("모집 인원"),
                                fieldWithPath("wishGetResponses[].updatedAt").type(
                                                STRING)
                                        .description("작성 날짜")

                        ))
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("찜 목록을 조회할 수 있다. - 이후 요청")
    void success_getWishesAfterFirst() throws Exception {
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

        int size = 3;
        List<Wish> wishes = WishDataFactory.createWishes(member, accompanyPost, size);
        wishRepository.saveAll(wishes);

        long maxId = -1L;
        for (Wish wish : wishes) {
            maxId = Math.max(maxId, wish.getId());
        }

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/wishes").header(
                                "Authorization", accessToken)
                        .param("cursorId", String.valueOf(maxId + 1))
                        .param("size", String.valueOf(size))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getWishesAfterFirst",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("cursorId").description("마지막으로 받은 찜 아이디"),
                                parameterWithName("size").description(
                                        "조회할 찜 개수, 값 넣지 않으면 기본 10개").optional()
                        ),
                        responseFields(
                                fieldWithPath("hasNext").type(BOOLEAN)
                                        .description("다음 찜 정보 존재 여부"),
                                fieldWithPath("wishGetResponses").type(ARRAY)
                                        .description("찜한 동행 구인글 목록"),
                                fieldWithPath("wishGetResponses[].wishId").type(NUMBER)
                                        .description("찜 아이디"),
                                fieldWithPath("wishGetResponses[].accompanyPostId").type(NUMBER)
                                        .description("찜한 동행 구인글 아이디"),
                                fieldWithPath("wishGetResponses[].title").type(
                                                STRING)
                                        .description("제목"),
                                fieldWithPath("wishGetResponses[].content").type(
                                                STRING)
                                        .description("내용"),
                                fieldWithPath("wishGetResponses[].totalPeople").type(
                                                NUMBER)
                                        .description("모집 인원"),
                                fieldWithPath("wishGetResponses[].updatedAt").type(
                                                STRING)
                                        .description("작성 날짜")

                        ))
                );
    }
}
