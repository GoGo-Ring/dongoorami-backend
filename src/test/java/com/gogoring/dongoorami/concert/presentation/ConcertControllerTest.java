package com.gogoring.dongoorami.concert.presentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogoring.dongoorami.concert.ConcertDataFactory;
import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.domain.ConcertReview;
import com.gogoring.dongoorami.concert.dto.request.ConcertReviewRequest;
import com.gogoring.dongoorami.concert.repository.ConcertRepository;
import com.gogoring.dongoorami.concert.repository.ConcertReviewRepository;
import com.gogoring.dongoorami.global.customMockUser.WithCustomMockUser;
import com.gogoring.dongoorami.global.jwt.TokenProvider;
import com.gogoring.dongoorami.member.MemberDataFactory;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
public class ConcertControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertReviewRepository concertReviewRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        concertReviewRepository.deleteAll();
        concertRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        concertReviewRepository.deleteAll();
        concertRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @WithCustomMockUser
    @DisplayName("공연 후기를 생성할 수 있다.")
    void success_createConcertReview() throws Exception {
        // given
        Member member = MemberDataFactory.createLoginMember();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());

        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        ConcertReviewRequest concertReviewRequest = new ConcertReviewRequest();
        ReflectionTestUtils.setField(concertReviewRequest, "title", "정말 재미있는 공연");
        ReflectionTestUtils.setField(concertReviewRequest, "content",
                "돈이 아깝지 않습니다. 다들 꼭 보러가세요!");
        ReflectionTestUtils.setField(concertReviewRequest, "rating", 3);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/concerts/reviews/{concertId}", concert.getId()).header(
                                "Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(concertReviewRequest))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(document("{ClassName}/createConcertReview",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("concertId").description("후기를 작성할 공연 아이디")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING)
                                        .description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("내용"),
                                fieldWithPath("rating").type(JsonFieldType.NUMBER)
                                        .description("평점(1~5)")
                        ))
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("공연 후기 목록을 조회할 수 있다. - 최초 요청")
    void success_getConcertReviewsFirst() throws Exception {
        // given
        Member member = MemberDataFactory.createLoginMemberWithNickname();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());

        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        int size = 3;
        List<ConcertReview> concertReviews = ConcertDataFactory.createConcertReviews(concert,
                member,
                size);
        concertReviewRepository.saveAll(concertReviews);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/concerts/reviews/{concertId}", concert.getId()).header(
                                "Authorization", accessToken)
                        .param("size", String.valueOf(size))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getConcertReviewsFirst",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("concertId").description("후기를 조회할 공연 아이디")
                        ),
                        queryParameters(
                                parameterWithName("size").description(
                                        "조회할 공연 후기 개수, 값 넣지 않으면 기본 10개").optional()
                        ),
                        responseFields(
                                fieldWithPath("hasNext").type(BOOLEAN)
                                        .description("다음 공연 후기 존재 여부"),
                                fieldWithPath("concertReviewGetResponses").type(ARRAY)
                                        .description("공연 후기 목록"),
                                fieldWithPath("concertReviewGetResponses[].id").type(NUMBER)
                                        .description("공연 후기 아이디"),
                                fieldWithPath("concertReviewGetResponses[].nickname").type(
                                                STRING)
                                        .description("작성자 닉네임"),
                                fieldWithPath("concertReviewGetResponses[].title").type(
                                                STRING)
                                        .description("제목"),
                                fieldWithPath("concertReviewGetResponses[].content").type(
                                                STRING)
                                        .description("내용"),
                                fieldWithPath("concertReviewGetResponses[].rating").type(
                                                NUMBER)
                                        .description("평점"),
                                fieldWithPath("concertReviewGetResponses[].isWriter").type(BOOLEAN)
                                        .description("본인 작성 여부"),
                                fieldWithPath("concertReviewGetResponses[].updatedAt").type(
                                                STRING)
                                        .description("작성 날짜")

                        ))
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("공연 후기 목록을 조회할 수 있다. - 이후 요청")
    void getConcertReviewsAfterFirst() throws Exception {
        // given
        Member member = MemberDataFactory.createLoginMemberWithNickname();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());

        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        int size = 3;
        List<ConcertReview> concertReviews = ConcertDataFactory.createConcertReviews(concert,
                member,
                size);
        concertReviewRepository.saveAll(concertReviews);
        long maxId = -1L;
        for (ConcertReview concertReview : concertReviews) {
            maxId = Math.max(maxId, concertReview.getId());
        }

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/concerts/reviews/{concertId}", concert.getId()).header(
                                "Authorization", accessToken)
                        .param("cursorId", String.valueOf(maxId + 1))
                        .param("size", String.valueOf(size))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getConcertReviewsAfterFirst",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("concertId").description("후기를 조회할 공연 아이디")
                        ),
                        queryParameters(
                                parameterWithName("cursorId").description("마지막으로 받은 공연 후기 아이디"),
                                parameterWithName("size").description(
                                        "조회할 공연 후기 개수, 값 넣지 않으면 기본 10개").optional()
                        ),
                        responseFields(
                                fieldWithPath("hasNext").type(BOOLEAN)
                                        .description("다음 공연 후기 존재 여부"),
                                fieldWithPath("concertReviewGetResponses").type(ARRAY)
                                        .description("공연 후기 목록"),
                                fieldWithPath("concertReviewGetResponses[].id").type(NUMBER)
                                        .description("공연 후기 아이디"),
                                fieldWithPath("concertReviewGetResponses[].nickname").type(
                                                STRING)
                                        .description("작성자 닉네임"),
                                fieldWithPath("concertReviewGetResponses[].title").type(
                                                STRING)
                                        .description("제목"),
                                fieldWithPath("concertReviewGetResponses[].content").type(
                                                STRING)
                                        .description("내용"),
                                fieldWithPath("concertReviewGetResponses[].rating").type(
                                                NUMBER)
                                        .description("평점"),
                                fieldWithPath("concertReviewGetResponses[].isWriter").type(BOOLEAN)
                                        .description("본인 작성 여부"),
                                fieldWithPath("concertReviewGetResponses[].updatedAt").type(
                                                STRING)
                                        .description("작성 날짜")

                        ))
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("공연 후기를 수정할 수 있다.")
    void success_updateConcertReview() throws Exception {
        // given
        Member member = MemberDataFactory.createLoginMemberWithNickname();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());

        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        ConcertReview concertReview = ConcertDataFactory.createConcertReviews(concert, member, 1)
                .get(0);
        concertReviewRepository.save(concertReview);

        ConcertReviewRequest concertReviewRequest = new ConcertReviewRequest();
        ReflectionTestUtils.setField(concertReviewRequest, "title", "테스트 제목");
        ReflectionTestUtils.setField(concertReviewRequest, "content", "테스트 내용");
        ReflectionTestUtils.setField(concertReviewRequest, "rating", 3);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/concerts/reviews/{concertReviewId}", concertReview.getId()).header(
                                "Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(concertReviewRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/updateConcertReview",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("concertReviewId").description("수정할 공연 후기 아이디")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING)
                                        .description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("내용"),
                                fieldWithPath("rating").type(JsonFieldType.NUMBER)
                                        .description("평점(1~5)")
                        ))
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("공연 후기를 삭제할 수 있다.")
    void success_deleteConcertReview() throws Exception {
        // given
        Member member = MemberDataFactory.createLoginMemberWithNickname();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());

        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        ConcertReview concertReview = ConcertDataFactory.createConcertReviews(concert, member, 1)
                .get(0);
        concertReviewRepository.save(concertReview);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/concerts/reviews/{concertReviewId}", concertReview.getId()).header(
                                "Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/deleteConcertReview",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("concertReviewId").description("삭제할 공연 후기 아이디")
                        ))
                );
    }
}
