package com.gogoring.dongoorami.concert.presentation;

import static com.gogoring.dongoorami.accompany.AccompanyDataFactory.createAccompanyComment;
import static com.gogoring.dongoorami.accompany.AccompanyDataFactory.createAccompanyPosts;
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
import com.gogoring.dongoorami.accompany.AccompanyDataFactory;
import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.domain.AccompanyReview;
import com.gogoring.dongoorami.accompany.domain.AccompanyReview.AccompanyReviewStatusType;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyCommentRequest;
import com.gogoring.dongoorami.accompany.repository.AccompanyCommentRepository;
import com.gogoring.dongoorami.accompany.repository.AccompanyPostRepository;
import com.gogoring.dongoorami.accompany.repository.AccompanyReviewRepository;
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
import java.util.ArrayList;
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
    private AccompanyPostRepository accompanyPostRepository;

    @Autowired
    private AccompanyCommentRepository accompanyCommentRepository;

    @Autowired
    private AccompanyReviewRepository accompanyReviewRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        accompanyReviewRepository.deleteAll();
        accompanyCommentRepository.deleteAll();
        accompanyPostRepository.deleteAll();
        concertReviewRepository.deleteAll();
        concertRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        accompanyReviewRepository.deleteAll();
        accompanyCommentRepository.deleteAll();
        accompanyPostRepository.deleteAll();
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
    @DisplayName("공연 후기 목록을 조회할 수 있다. - 최초 요청")
    void success_getConcertReviewsFirst() throws Exception {
        // given
        Member member = MemberDataFactory.createMember();
        ReflectionTestUtils.setField(member, "nickname", "백둥이");
        memberRepository.save(member);

        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        int size = 3;
        List<ConcertReview> concertReviews = ConcertDataFactory.createConcertReviews(concert,
                member,
                size);
        concertReviewRepository.saveAll(concertReviews);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/concerts/reviews/{concertId}", concert.getId())
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
    @DisplayName("공연 후기 목록을 조회할 수 있다. - 이후 요청")
    void getConcertReviewsAfterFirst() throws Exception {
        // given
        Member member = MemberDataFactory.createMember();
        ReflectionTestUtils.setField(member, "nickname", "백둥이");
        memberRepository.save(member);

        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        int size = 3;
        List<ConcertReview> concertReviews = ConcertDataFactory.createConcertReviews(concert,
                member, size);
        concertReviewRepository.saveAll(concertReviews);
        long maxId = -1L;
        for (ConcertReview concertReview : concertReviews) {
            maxId = Math.max(maxId, concertReview.getId());
        }

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/concerts/reviews/{concertId}", concert.getId())
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

    @Test
    @DisplayName("공연 단건 상세 조회를 할 수 있다.")
    void success_getConcert() throws Exception {
        // given
        Member member = MemberDataFactory.createMember();
        memberRepository.save(member);

        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/concerts/{concertId}", concert.getId())
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getConcert",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("concertId").description("조회할 공연 아이디")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER)
                                        .description("공연 아이디"),
                                fieldWithPath("name").type(STRING)
                                        .description("공연명"),
                                fieldWithPath("startedAt").type(STRING)
                                        .description("공연 시작 일자"),
                                fieldWithPath("endedAt").type(STRING)
                                        .description("공연 종료 일자"),
                                fieldWithPath("place").type(STRING)
                                        .description("공연 장소"),
                                fieldWithPath("actor").type(STRING)
                                        .description("출연진"),
                                fieldWithPath("crew").type(STRING)
                                        .description("제작진"),
                                fieldWithPath("runtime").type(STRING)
                                        .description("공연 런타임"),
                                fieldWithPath("age").type(STRING)
                                        .description("공연 관람 연령"),
                                fieldWithPath("producer").type(STRING)
                                        .description("제작사"),
                                fieldWithPath("agency").type(STRING)
                                        .description("기획사"),
                                fieldWithPath("host").type(STRING)
                                        .description("주최"),
                                fieldWithPath("management").type(STRING)
                                        .description("주관"),
                                fieldWithPath("cost").type(STRING)
                                        .description("티켓 가격"),
                                fieldWithPath("poster").type(STRING)
                                        .description("포스터 이미지 url"),
                                fieldWithPath("summary").type(STRING)
                                        .description("줄거리"),
                                fieldWithPath("genre").type(STRING)
                                        .description("장르"),
                                fieldWithPath("status").type(STRING)
                                        .description("공연 상태"),
                                fieldWithPath("introductionImages").type(ARRAY)
                                        .description("소개 이미지 url 목록"),
                                fieldWithPath("schedule").type(STRING)
                                        .description("공연 시간"),
                                fieldWithPath("totalAccompanies").type(NUMBER)
                                        .description("공연 구인글 수"),
                                fieldWithPath("totalReviews").type(NUMBER)
                                        .description("관람 후기 수")
                        ))
                );
    }

    @Test
    @DisplayName("공연 목록을 조회할 수 있다. - 최초 요청")
    void success_getConcertsFirst() throws Exception {
        // given
        Member member = MemberDataFactory.createMember();
        memberRepository.save(member);

        int size = 3;
        List<Concert> concerts = ConcertDataFactory.createConcerts(size);
        concertRepository.saveAll(concerts);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/concerts")
                        .param("size", String.valueOf(size))
                        .param("keyword", concerts.get(0).getName()
                                .substring(0, concerts.get(0).getName().length() / 2))
                        .param("genres", concerts.get(0).getGenre())
                        .param("genres", "복합")
                        .param("statuses", concerts.get(0).getStatus())
                        .param("statuses", "공연종료")
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getConcertsFirst",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("size").description(
                                        "조회할 공연 개수, 값 넣지 않으면 기본 6개").optional(),
                                parameterWithName("keyword").description(
                                        "조회할 공연명, 값 넣지 않으면 전체 검색").optional(),
                                parameterWithName("genres").description(
                                        "조회할 장르, 값 넣지 않으면 전체 검색").optional(),
                                parameterWithName("statuses").description(
                                        "조회할 공연 상태, 값 넣지 않으면 전체 검색").optional()
                        ),
                        responseFields(
                                fieldWithPath("hasNext").type(BOOLEAN)
                                        .description("다음 공연 존재 여부"),
                                fieldWithPath("concertGetShortResponses").type(ARRAY)
                                        .description("공연 목록"),
                                fieldWithPath("concertGetShortResponses[].id").type(NUMBER)
                                        .description("공연 아이디"),
                                fieldWithPath("concertGetShortResponses[].name").type(STRING)
                                        .description("공연명"),
                                fieldWithPath("concertGetShortResponses[].place").type(STRING)
                                        .description("공연장소"),
                                fieldWithPath("concertGetShortResponses[].genre").type(STRING)
                                        .description("장르"),
                                fieldWithPath("concertGetShortResponses[].startedAt").type(STRING)
                                        .description("공연 시작 일자"),
                                fieldWithPath("concertGetShortResponses[].endedAt").type(STRING)
                                        .description("공연 종료 일자"),
                                fieldWithPath("concertGetShortResponses[].poster").type(STRING)
                                        .description("포스터 이미지 url"),
                                fieldWithPath("concertGetShortResponses[].status").type(STRING)
                                        .description("공연 상태")
                        ))
                );
    }

    @Test
    @DisplayName("공연 목록을 조회할 수 있다. - 이후 요청")
    void success_getConcertsAfterFirst() throws Exception {
        // given
        Member member = MemberDataFactory.createMember();
        memberRepository.save(member);

        int size = 3;
        List<Concert> concerts = ConcertDataFactory.createConcerts(size);
        concertRepository.saveAll(concerts);
        long maxId = -1L;
        for (Concert concert : concerts) {
            maxId = Math.max(maxId, concert.getId());
        }

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/concerts")
                        .param("cursorId", String.valueOf(maxId))
                        .param("size", String.valueOf(size))
                        .param("keyword", concerts.get(0).getName()
                                .substring(0, concerts.get(0).getName().length() / 2))
                        .param("genres", concerts.get(0).getGenre())
                        .param("genres", "복합")
                        .param("statuses", concerts.get(0).getStatus())
                        .param("statuses", "공연종료")
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getConcertsAfterFirst",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("cursorId").description("마지막으로 받은 공연 아이디"),
                                parameterWithName("size").description(
                                        "조회할 공연 개수, 값 넣지 않으면 기본 6개").optional(),
                                parameterWithName("keyword").description(
                                        "조회할 공연명, 값 넣지 않으면 전체 검색").optional(),
                                parameterWithName("genres").description(
                                        "조회할 장르, 값 넣지 않으면 전체 검색").optional(),
                                parameterWithName("statuses").description(
                                        "조회할 공연 상태, 값 넣지 않으면 전체 검색").optional()
                        ),
                        responseFields(
                                fieldWithPath("hasNext").type(BOOLEAN)
                                        .description("다음 공연 존재 여부"),
                                fieldWithPath("concertGetShortResponses").type(ARRAY)
                                        .description("공연 목록"),
                                fieldWithPath("concertGetShortResponses[].id").type(NUMBER)
                                        .description("공연 아이디"),
                                fieldWithPath("concertGetShortResponses[].name").type(STRING)
                                        .description("공연명"),
                                fieldWithPath("concertGetShortResponses[].place").type(STRING)
                                        .description("공연장소"),
                                fieldWithPath("concertGetShortResponses[].genre").type(STRING)
                                        .description("장르"),
                                fieldWithPath("concertGetShortResponses[].startedAt").type(STRING)
                                        .description("공연 시작 일자"),
                                fieldWithPath("concertGetShortResponses[].endedAt").type(STRING)
                                        .description("공연 종료 일자"),
                                fieldWithPath("concertGetShortResponses[].poster").type(STRING)
                                        .description("포스터 이미지 url"),
                                fieldWithPath("concertGetShortResponses[].status").type(STRING)
                                        .description("공연 상태")
                        ))
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("키워드로 공연 목록을 조회할 수 있다.")
    void success_getConcertsByKeyword() throws Exception {
        // given
        Member member = MemberDataFactory.createLoginMemberWithNickname();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());

        int size = 7;
        List<Concert> concerts = ConcertDataFactory.createConcerts(size);
        concertRepository.saveAll(concerts);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/concerts/keywords").header(
                                "Authorization", accessToken)
                        .param("keyword", "고고링")
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getConcertsByKeyword",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("keyword").description("공연 검색 키워드")
                        ),
                        responseFields(
                                fieldWithPath("[]").type(ARRAY)
                                        .description("공연 정보 목록"),
                                fieldWithPath("[].id").type(NUMBER)
                                        .description("공연 아이디"),
                                fieldWithPath("[].name").type(STRING)
                                        .description("공연 이름"),
                                fieldWithPath("[].place").type(STRING)
                                        .description("공연 장소")
                        ))
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("특정 회원이 작성한 공연/동행 구인 후기를 조회할 수 있다.")
    void success_getConcertAndAccompanyReviews() throws Exception {
        // given
        Member member1 = MemberDataFactory.createLoginMember();
        Member member2 = MemberDataFactory.createMember();
        Member member3 = MemberDataFactory.createMember();
        memberRepository.saveAll(List.of(member1, member2, member3));
        String accessToken = tokenProvider.createAccessToken(member1.getProviderId(),
                member1.getRoles());

        Concert concert = concertRepository.save(ConcertDataFactory.createConcert());

        List<ConcertReview> concertReviews = ConcertDataFactory.createConcertReviews(concert,
                member1, 2);
        concertReviewRepository.saveAll(concertReviews);

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
        ReflectionTestUtils.setField(accompanyReview1, "content", "친절한 분이셨습니다~");
        ReflectionTestUtils.setField(accompanyReview2, "content", "덕분에 공연 재밌게 봤어요!");
        ReflectionTestUtils.setField(accompanyReview1, "status",
                AccompanyReviewStatusType.AFTER_ACCOMPANY_AND_WRITTEN);
        ReflectionTestUtils.setField(accompanyReview2, "status",
                AccompanyReviewStatusType.AFTER_ACCOMPANY_AND_WRITTEN);
        accompanyReviewRepository.saveAll(List.of(accompanyReview1, accompanyReview2));

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/concerts/accompanies/reviews").header(
                        "Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getConcertAndAccompanyReviews",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[]").type(ARRAY)
                                        .description("공연/동행 구인 후기 목록"),
                                fieldWithPath("[].reviewId").type(NUMBER)
                                        .description("후기 아이디"),
                                fieldWithPath("[].targetId").type(NUMBER)
                                        .description("후기가 작성된 공연/동행 구인글 아이디"),
                                fieldWithPath("[].title").type(STRING)
                                        .description("공연 이름/동행 구인글 제목"),
                                fieldWithPath("[].content").type(STRING)
                                        .description("후기 내용"),
                                fieldWithPath("[].updatedAt").type(STRING)
                                        .description("작성 날짜"),
                                fieldWithPath("[].isAccompanyReview").type(BOOLEAN)
                                        .description("동행 후기/공연 후기 여부, 동행 후기면 true, 공연 후기면 false")
                        ))
                );
    }

    @Test
    @DisplayName("공연 사진 5개를 조회할 수 있다.")
    void success_getConcertImages() throws Exception {
        // given
        List<Concert> concerts = ConcertDataFactory.createConcerts(5);
        concertRepository.saveAll(concerts);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/concerts/images")
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getConcertImages",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[]").type(ARRAY)
                                        .description("공연 사진 정보 목록"),
                                fieldWithPath("[].id").type(NUMBER)
                                        .description("공연 아이디"),
                                fieldWithPath("[].imageUrl").type(STRING)
                                        .description("공연 포스터 url")
                        ))
                );
    }

    @Test
    @DisplayName("키워드로 동행 구인글과 공연 목록을 조회할 수 있다. - 최초 요청")
    void success_getAccompanyPostsAndConcertsByKeywordFirst() throws Exception {
        // given
        Member member = MemberDataFactory.createMember();
        memberRepository.save(member);

        int size = 3;
        List<Concert> concerts = concertRepository.saveAll(ConcertDataFactory.createConcerts(size));
        accompanyPostRepository.saveAll(createAccompanyPosts(member, size, concerts.get(0)));
        String keyword = concerts.get(0).getName()
                .substring(0, concerts.get(0).getName().length() / 2);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/accompanies-concerts")
                        .param("size", String.valueOf(size))
                        .param("keyword", keyword)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getAccompanyPostsAndConcertsByKeywordFirst",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("size").description(
                                        "조회할 동행구인글/공연 각각의 개수, 값 넣지 않으면 기본 10개").optional(),
                                parameterWithName("keyword").description(
                                        "검색 키워드").optional()
                        ),
                        responseFields(
                                fieldWithPath("hasNextAccompanyPost").type(BOOLEAN)
                                        .description("다음 동행 구인글 존재 여부"),
                                fieldWithPath("hasNextConcert").type(BOOLEAN)
                                        .description("다음 공연 존재 여부"),
                                fieldWithPath("concertGetShortResponses").type(ARRAY)
                                        .description("공연 목록"),
                                fieldWithPath("concertGetShortResponses[].id").type(NUMBER)
                                        .description("공연 아이디"),
                                fieldWithPath("concertGetShortResponses[].name").type(STRING)
                                        .description("공연명"),
                                fieldWithPath("concertGetShortResponses[].place").type(STRING)
                                        .description("공연장소"),
                                fieldWithPath("concertGetShortResponses[].genre").type(STRING)
                                        .description("장르"),
                                fieldWithPath("concertGetShortResponses[].startedAt").type(STRING)
                                        .description("공연 시작 일자"),
                                fieldWithPath("concertGetShortResponses[].endedAt").type(STRING)
                                        .description("공연 종료 일자"),
                                fieldWithPath("concertGetShortResponses[].poster").type(STRING)
                                        .description("포스터 이미지 url"),
                                fieldWithPath("concertGetShortResponses[].status").type(STRING)
                                        .description("공연 상태"),
                                fieldWithPath("accompanyPostInfos").type(ARRAY)
                                        .description("동행 구인글 목록"),
                                fieldWithPath("accompanyPostInfos[].id").type(NUMBER)
                                        .description("동행 구인글 id"),
                                fieldWithPath("accompanyPostInfos[].title").type(
                                                STRING)
                                        .description("제목"),
                                fieldWithPath("accompanyPostInfos[].writer").type(
                                                STRING)
                                        .description("작성자"),
                                fieldWithPath("accompanyPostInfos[].createdAt").type(
                                                STRING)
                                        .description("생성 날짜"),
                                fieldWithPath("accompanyPostInfos[].updatedAt").type(
                                                STRING)
                                        .description("수정 날짜"),
                                fieldWithPath("accompanyPostInfos[].status").type(
                                                STRING)
                                        .description("구인 상태"),
                                fieldWithPath("accompanyPostInfos[].concertName").type(
                                                STRING)
                                        .description("공연명"),
                                fieldWithPath("accompanyPostInfos[].viewCount").type(
                                                NUMBER)
                                        .description("조회수"),
                                fieldWithPath("accompanyPostInfos[].commentCount").type(
                                                NUMBER)
                                        .description("댓글수"),
                                fieldWithPath("accompanyPostInfos[].gender").type(
                                                STRING)
                                        .description("성별"),
                                fieldWithPath("accompanyPostInfos[].totalPeople").type(
                                                NUMBER)
                                        .description("모집 인원 수")
                        ))
                );
    }

    @Test
    @DisplayName("키워드로 동행 구인글과 공연 목록을 조회할 수 있다. - 이후 요청")
    void success_getAccompanyPostsAndConcertsByKeywordAfterFirst() throws Exception {
        // given
        Member member = MemberDataFactory.createMember();
        memberRepository.save(member);

        int size = 3;
        List<Concert> concerts = concertRepository.saveAll(ConcertDataFactory.createConcerts(size));
        Long concertCursorId = concerts.get(concerts.size() - 1).getId() + 1;
        List<AccompanyPost> accompanyPosts = accompanyPostRepository.saveAll(
                createAccompanyPosts(member, size, concerts.get(0)));
        Long accompanyPostCursorId = accompanyPosts.get(accompanyPosts.size() - 1).getId() + 1;
        String keyword = concerts.get(0).getName()
                .substring(0, concerts.get(0).getName().length() / 2);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/accompanies-concerts")
                        .param("accompanyPostCursorId", String.valueOf(accompanyPostCursorId))
                        .param("concertCursorId", String.valueOf(concertCursorId))
                        .param("size", String.valueOf(size))
                        .param("keyword", keyword)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getAccompanyPostsAndConcertsByKeywordAfterFirst",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("accompanyPostCursorId").description(
                                        "마지막으로 받은 동행 구인글 id").optional(),
                                parameterWithName("concertCursorId").description(
                                        "마지막으로 받은 공연 id").optional(),
                                parameterWithName("size").description(
                                        "조회할 동행구인글/공연 각각의 개수, 값 넣지 않으면 기본 10개").optional(),
                                parameterWithName("keyword").description(
                                        "검색 키워드").optional()
                        ),
                        responseFields(
                                fieldWithPath("hasNextAccompanyPost").type(BOOLEAN)
                                        .description("다음 동행 구인글 존재 여부"),
                                fieldWithPath("hasNextConcert").type(BOOLEAN)
                                        .description("다음 공연 존재 여부"),
                                fieldWithPath("concertGetShortResponses").type(ARRAY)
                                        .description("공연 목록"),
                                fieldWithPath("concertGetShortResponses[].id").type(NUMBER)
                                        .description("공연 아이디"),
                                fieldWithPath("concertGetShortResponses[].name").type(STRING)
                                        .description("공연명"),
                                fieldWithPath("concertGetShortResponses[].place").type(STRING)
                                        .description("공연장소"),
                                fieldWithPath("concertGetShortResponses[].genre").type(STRING)
                                        .description("장르"),
                                fieldWithPath("concertGetShortResponses[].startedAt").type(STRING)
                                        .description("공연 시작 일자"),
                                fieldWithPath("concertGetShortResponses[].endedAt").type(STRING)
                                        .description("공연 종료 일자"),
                                fieldWithPath("concertGetShortResponses[].poster").type(STRING)
                                        .description("포스터 이미지 url"),
                                fieldWithPath("concertGetShortResponses[].status").type(STRING)
                                        .description("공연 상태"),
                                fieldWithPath("accompanyPostInfos").type(ARRAY)
                                        .description("동행 구인글 목록"),
                                fieldWithPath("accompanyPostInfos[].id").type(NUMBER)
                                        .description("동행 구인글 id"),
                                fieldWithPath("accompanyPostInfos[].title").type(
                                                STRING)
                                        .description("제목"),
                                fieldWithPath("accompanyPostInfos[].writer").type(
                                                STRING)
                                        .description("작성자"),
                                fieldWithPath("accompanyPostInfos[].createdAt").type(
                                                STRING)
                                        .description("생성 날짜"),
                                fieldWithPath("accompanyPostInfos[].updatedAt").type(
                                                STRING)
                                        .description("수정 날짜"),
                                fieldWithPath("accompanyPostInfos[].status").type(
                                                STRING)
                                        .description("구인 상태"),
                                fieldWithPath("accompanyPostInfos[].concertName").type(
                                                STRING)
                                        .description("공연명"),
                                fieldWithPath("accompanyPostInfos[].viewCount").type(
                                                NUMBER)
                                        .description("조회수"),
                                fieldWithPath("accompanyPostInfos[].commentCount").type(
                                                NUMBER)
                                        .description("댓글수"),
                                fieldWithPath("accompanyPostInfos[].gender").type(
                                                STRING)
                                        .description("성별"),
                                fieldWithPath("accompanyPostInfos[].totalPeople").type(
                                                NUMBER)
                                        .description("모집 인원 수")
                        ))
                );
    }
}
