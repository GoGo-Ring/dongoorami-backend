package com.gogoring.dongoorami.accompany.presentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.accompany.repository.AccompanyPostRepository;
import com.gogoring.dongoorami.global.customMockUser.WithCustomMockUser;
import com.gogoring.dongoorami.global.jwt.TokenProvider;
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
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
class AccompanyControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccompanyPostRepository accompanyPostRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithCustomMockUser
    @DisplayName("동행 구인글을 게시할 수 있다.")
    void success_createAccompanyPost() throws Exception {
        // given
        Member member = (Member) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());
        AccompanyPostRequest accompanyPostRequest = AccompanyPostRequest.builder()
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
                .totalPeople(2L)
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/accompany/posts")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(accompanyPostRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/createAccompanyPost",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING)
                                        .description("제목"),
                                fieldWithPath("concertName").type(JsonFieldType.STRING)
                                        .description("공연명"),
                                fieldWithPath("concertPlace").type(JsonFieldType.STRING)
                                        .description("공연장소"),
                                fieldWithPath("region").type(JsonFieldType.STRING)
                                        .description("지역"),
                                fieldWithPath("startAge").type(JsonFieldType.NUMBER)
                                        .description("시작 연령"),
                                fieldWithPath("endAge").type(JsonFieldType.NUMBER)
                                        .description("종료 연령"),
                                fieldWithPath("totalPeople").type(JsonFieldType.NUMBER)
                                        .description("인원 수"),
                                fieldWithPath("gender").type(JsonFieldType.STRING)
                                        .description("성별"),
                                fieldWithPath("startDate").type(JsonFieldType.STRING)
                                        .description("시작 날짜")
                                , fieldWithPath("endDate").type(JsonFieldType.STRING)
                                        .description("종료 날짜")
                                , fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("내용")
                        ))

                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("동행 구인글 목록을 조회할 수 있다. - 최초 요청")
    void success_getAccompanyPostsFirst() throws Exception {
        // given
        Member member = (Member) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());
        String postCnt = "3";
        accompanyPostRepository.saveAll(createAccompanyPosts(member, 5));

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/accompany/posts")
                        .header("Authorization", accessToken)
                        .param("size", postCnt)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getAccompanyPostsFirst",
                        preprocessResponse(prettyPrint()),
                        queryParameters(parameterWithName("size").description("요청할 동행 구인글 개수")),
                        responseFields(
                                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN)
                                        .description("다음 동행 구인글 존재 여부"),
                                fieldWithPath("accompanyPostInfos").type(JsonFieldType.ARRAY)
                                        .description("동행 구인글 정보 목록"),
                                fieldWithPath("accompanyPostInfos[].id").type(JsonFieldType.NUMBER)
                                        .description("동행 구인글 id"),
                                fieldWithPath("accompanyPostInfos[].title").type(
                                                JsonFieldType.STRING)
                                        .description("제목"),
                                fieldWithPath("accompanyPostInfos[].writer").type(
                                                JsonFieldType.STRING)
                                        .description("작성자"),
                                fieldWithPath("accompanyPostInfos[].updatedAt").type(
                                                JsonFieldType.STRING)
                                        .description("작성 날짜"),
                                fieldWithPath("accompanyPostInfos[].status").type(
                                                JsonFieldType.STRING)
                                        .description("구인 상태"),
                                fieldWithPath("accompanyPostInfos[].concertName").type(
                                                JsonFieldType.STRING)
                                        .description("공연명"),
                                fieldWithPath("accompanyPostInfos[].viewCount").type(
                                                JsonFieldType.NUMBER)
                                        .description("조회수"),
                                fieldWithPath("accompanyPostInfos[].commentCount").type(
                                                JsonFieldType.NUMBER)
                                        .description("댓글수"),
                                fieldWithPath("accompanyPostInfos[].gender").type(
                                                JsonFieldType.STRING)
                                        .description("성별"),
                                fieldWithPath("accompanyPostInfos[].totalPeople").type(
                                                JsonFieldType.NUMBER)
                                        .description("모집 인원 수")
                        )
                ));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("동행 구인글 목록을 조회할 수 있다. - 이후 요청")
    void success_getAccompanyPostsAfterFirst() throws Exception {
        // given
        Member member = (Member) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());
        String cursorId = "17", postCnt = "3";
        accompanyPostRepository.saveAll(createAccompanyPosts(member, 5));

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/accompany/posts")
                        .header("Authorization", accessToken)
                        .param("cursorId", cursorId)
                        .param("size", postCnt)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getAccompanyPostsAfterFirst",
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("cursorId").description("마지막으로 받은 동행 구인글 id"),
                                parameterWithName("size").description("요청할 동행 구인글 개수")),
                        responseFields(
                                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN)
                                        .description("다음 동행 구인글 존재 여부"),
                                fieldWithPath("accompanyPostInfos").type(JsonFieldType.ARRAY)
                                        .description("동행 구인글 정보 목록"),
                                fieldWithPath("accompanyPostInfos[].id").type(JsonFieldType.NUMBER)
                                        .description("동행 구인글 id"),
                                fieldWithPath("accompanyPostInfos[].title").type(
                                                JsonFieldType.STRING)
                                        .description("제목"),
                                fieldWithPath("accompanyPostInfos[].writer").type(
                                                JsonFieldType.STRING)
                                        .description("작성자"),
                                fieldWithPath("accompanyPostInfos[].updatedAt").type(
                                                JsonFieldType.STRING)
                                        .description("작성 날짜"),
                                fieldWithPath("accompanyPostInfos[].status").type(
                                                JsonFieldType.STRING)
                                        .description("구인 상태"),
                                fieldWithPath("accompanyPostInfos[].concertName").type(
                                                JsonFieldType.STRING)
                                        .description("공연명"),
                                fieldWithPath("accompanyPostInfos[].viewCount").type(
                                                JsonFieldType.NUMBER)
                                        .description("조회수"),
                                fieldWithPath("accompanyPostInfos[].commentCount").type(
                                                JsonFieldType.NUMBER)
                                        .description("댓글수"),
                                fieldWithPath("accompanyPostInfos[].gender").type(
                                                JsonFieldType.STRING)
                                        .description("성별"),
                                fieldWithPath("accompanyPostInfos[].totalPeople").type(
                                                JsonFieldType.NUMBER)
                                        .description("모집 인원 수")
                        )
                ));
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