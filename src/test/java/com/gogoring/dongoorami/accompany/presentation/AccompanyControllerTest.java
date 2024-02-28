package com.gogoring.dongoorami.accompany.presentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.formParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.accompany.repository.AccompanyPostRepository;
import com.gogoring.dongoorami.global.customMockUser.WithCustomMockUser;
import com.gogoring.dongoorami.global.jwt.CustomUserDetails;
import com.gogoring.dongoorami.global.jwt.TokenProvider;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import java.io.FileInputStream;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

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
        Member member = ((CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()).getMember();
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
                .images(createMockMultipartFiles(2))
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/v1/accompany/posts")
                        .file("images", accompanyPostRequest.getImages().get(0).getBytes())
                        .file("images", accompanyPostRequest.getImages().get(1).getBytes())
                        .param("concertName", accompanyPostRequest.getConcertName())
                        .param("concertPlace", accompanyPostRequest.getConcertPlace())
                        .param("startDate", accompanyPostRequest.getStartDate().toString())
                        .param("endDate", accompanyPostRequest.getEndDate().toString())
                        .param("gender", accompanyPostRequest.getGender())
                        .param("region", accompanyPostRequest.getRegion())
                        .param("content", accompanyPostRequest.getContent())
                        .param("startAge", accompanyPostRequest.getStartAge().toString())
                        .param("endAge", accompanyPostRequest.getEndAge().toString())
                        .param("totalPeople", accompanyPostRequest.getTotalPeople().toString())
                        .param("title", accompanyPostRequest.getTitle())
                        .with(csrf().asHeader())
                        .header("Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(document("{ClassName}/createAccompanyPost",
                                preprocessRequest(prettyPrint()),
                                requestParts(
                                        partWithName("images").description("이미지 0개 이상")
                                ),
                                formParameters(
                                        parameterWithName("concertName").description("공연명").optional(),
                                        parameterWithName("concertPlace").description("공연 장소").optional(),
                                        parameterWithName("startDate").description("시작 날짜").optional(),
                                        parameterWithName("endDate").description("종료 날짜").optional(),
                                        parameterWithName("gender").description("성별").optional(),
                                        parameterWithName("region").description("공연 지역").optional(),
                                        parameterWithName("content").description("내용").optional(),
                                        parameterWithName("startAge").description("시작 연령").optional(),
                                        parameterWithName("endAge").description("종료 연령").optional(),
                                        parameterWithName("totalPeople").description("인원 수").optional(),
                                        parameterWithName("title").description("제목").optional()
                                )
                        )
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("동행 구인글 목록을 조회할 수 있다. - 최초 요청")
    void success_getAccompanyPostsFirst() throws Exception {
        // given
        Member member = ((CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()).getMember();
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
                                fieldWithPath("hasNext").type(BOOLEAN)
                                        .description("다음 동행 구인글 존재 여부"),
                                fieldWithPath("accompanyPostInfos").type(ARRAY)
                                        .description("동행 구인글 정보 목록"),
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
                        )
                ));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("동행 구인글 목록을 조회할 수 있다. - 이후 요청")
    void success_getAccompanyPostsAfterFirst() throws Exception {
        // given
        Member member = ((CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()).getMember();
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
                                fieldWithPath("accompanyPostInfos").type(ARRAY)
                                        .description("동행 구인글 정보 목록"),
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

    private List<MultipartFile> createMockMultipartFiles(int size) throws Exception {
        List<MultipartFile> images = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            images.add(new MockMultipartFile("image", "김영한.JPG",
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    new FileInputStream("src/test/resources/김영한.JPG")));
        }

        return images;
    }

}