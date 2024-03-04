package com.gogoring.dongoorami.accompany.presentation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
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
import static org.springframework.restdocs.request.RequestDocumentation.formParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyCommentRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.accompany.repository.AccompanyCommentRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
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
    private AccompanyCommentRepository accompanyCommentRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${cloud.aws.s3.default-image-url}")
    private String defaultImageUrl;

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
                        .file((MockMultipartFile) accompanyPostRequest.getImages().get(0))
                        .file((MockMultipartFile) accompanyPostRequest.getImages().get(1))
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

    @Test
    @WithCustomMockUser
    @DisplayName("동행 구인글을 단건 상세 조회할 수 있다.")
    void success_getAccompanyPost() throws Exception {
        // given
        Member member = ((CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()).getMember();
        ReflectionTestUtils.setField(member, "gender", "여자");
        ReflectionTestUtils.setField(member, "birthDate", LocalDate.of(2001, 1, 17));
        ReflectionTestUtils.setField(member, "introduction", "안녕하세요~");
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());
        AccompanyPost accompanyPost = accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 1)).get(0);
        Long beforeViewCount = accompanyPost.getViewCount();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/accompany/posts/{accompanyPostId}", accompanyPost.getId())
                        .header("Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getAccompanyPost",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("accompanyPostId").description("조회할 동행 구인글 id")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("동행 구인글 id"),
                                fieldWithPath("title").type(STRING).description("제목"),
                                fieldWithPath("memberInfo.id").type(NUMBER).description("작성자 id"),
                                fieldWithPath("memberInfo.name").type(STRING).description("작성자 이름"),
                                fieldWithPath("memberInfo.profileImage").type(STRING)
                                        .description("작성자 프로필 이미지 url"),
                                fieldWithPath("memberInfo.gender").type(STRING)
                                        .description("작성자 성별"),
                                fieldWithPath("memberInfo.age").type(NUMBER).description("작성자 나이"),
                                fieldWithPath("memberInfo.introduction").type(STRING)
                                        .description("작성자 소개"),
                                fieldWithPath("createdAt").type(STRING).description("생성 날짜"),
                                fieldWithPath("updatedAt").type(STRING).description("수정 날짜"),
                                fieldWithPath("viewCount").type(NUMBER).description("조회수"),
                                fieldWithPath("commentCount").type(NUMBER).description("댓글수"),
                                fieldWithPath("status").type(STRING).description("구인 상태"),
                                fieldWithPath("concertName").type(STRING).description("공연명"),
                                fieldWithPath("concertPlace").type(STRING).description("공연 장소"),
                                fieldWithPath("region").type(STRING).description("공연 지역"),
                                fieldWithPath("startAge").type(NUMBER).description("시작 연령"),
                                fieldWithPath("endAge").type(NUMBER).description("종료 연령"),
                                fieldWithPath("totalPeople").type(NUMBER).description("인원 수"),
                                fieldWithPath("gender").type(STRING).description("성별"),
                                fieldWithPath("startDate").type(STRING).description("시작 날짜"),
                                fieldWithPath("endDate").type(STRING).description("종료 날짜"),
                                fieldWithPath("waitingCount").type(NUMBER).description("대기 인원 수"),
                                fieldWithPath("content").type(STRING).description("내용"),
                                fieldWithPath("images").type(ARRAY).description("이미지 리스트"),
                                fieldWithPath("isWish").type(BOOLEAN).description("찜 여부")
                        )
                ));
        Long afterViewCount = accompanyPostRepository.findById(accompanyPost.getId()).get()
                .getViewCount();
        assertThat(afterViewCount, equalTo(beforeViewCount + 1));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("동행 구인글에 댓글을 작성할 수 있다.")
    void success_createAccompanyComment() throws Exception {
        // given
        Member member = ((CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()).getMember();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());
        AccompanyPost accompanyPost = accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 1)).get(0);
        AccompanyCommentRequest accompanyCommentRequest = new AccompanyCommentRequest(
                "가는 길만 동행해도 괜찮을까요!?");

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/accompany/comments/{accompanyPostId}", accompanyPost.getId())
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(accompanyCommentRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(document("{ClassName}/createAccompanyComment",
                        preprocessRequest(prettyPrint()),
                        pathParameters(
                                parameterWithName("accompanyPostId").description("동행 구인글 id")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("내용")
                        )
                ));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("특정 동행 구인글의 전체 댓글을 조회할 수 있다.")
    void success_getAccompanyComments() throws Exception {
        // given
        Member member = ((CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()).getMember();
        ReflectionTestUtils.setField(member, "gender", "여자");
        ReflectionTestUtils.setField(member, "birthDate", LocalDate.of(2001, 1, 17));
        ReflectionTestUtils.setField(member, "introduction", "안녕하세요~");
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());
        AccompanyPost accompanyPost = accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 1)).get(0);
        List<AccompanyComment> accompanyComments = createAccompanyComment(member, 3);
        accompanyComments.stream().forEach(accompanyPost::addAccompanyComment);
        accompanyCommentRepository.saveAll(accompanyComments);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/accompany/comments/{accompanyPostId}", accompanyPost.getId())
                        .header("Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getAccompanyComments",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("accompanyPostId").description("동행 구인글 id")
                        ),
                        responseFields(
                                fieldWithPath("accompanyCommentInfos").type(ARRAY)
                                        .description("동행 구인글 댓글 목록"),
                                fieldWithPath("accompanyCommentInfos[].id").type(NUMBER)
                                        .description("댓글 id"),
                                fieldWithPath("accompanyCommentInfos[].content").type(STRING)
                                        .description("내용"),
                                fieldWithPath("accompanyCommentInfos[].createdAt").type(STRING)
                                        .description("생성 날짜"),
                                fieldWithPath("accompanyCommentInfos[].updatedAt").type(STRING)
                                        .description("수정 날짜"),
                                fieldWithPath("accompanyCommentInfos[].memberInfo.id").type(NUMBER)
                                        .description("작성자 id"),
                                fieldWithPath("accompanyCommentInfos[].memberInfo.name").type(
                                        STRING).description("작성자 이름"),
                                fieldWithPath(
                                        "accompanyCommentInfos[].memberInfo.profileImage").type(
                                        STRING).description("작성자 프로필 이미지 url"),
                                fieldWithPath("accompanyCommentInfos[].memberInfo.gender").type(
                                        STRING).description("작성자 성별"),
                                fieldWithPath("accompanyCommentInfos[].memberInfo.age").type(NUMBER)
                                        .description("작성자 나이"),
                                fieldWithPath(
                                        "accompanyCommentInfos[].memberInfo.introduction").type(
                                        STRING).description("작성자 소개")
                        )
                ));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("작성자는 해당 동행 구인글을 수정할 수 있다.")
    void success_updateAccompanyPost() throws Exception {
        // given
        Member member = ((CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()).getMember();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());
        AccompanyPost accompanyPost = accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 1)).get(0);
        List<MultipartFile> mockMultipartFiles = createMockMultipartFiles(2);
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
                multipart("/api/v1/accompany/posts/{accompanyPostId}", accompanyPost.getId())
                        .file((MockMultipartFile) accompanyPostRequest.getImages().get(0))
                        .file((MockMultipartFile) accompanyPostRequest.getImages().get(1))
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
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/updateAccompanyPost",
                                preprocessRequest(prettyPrint()),
                                pathParameters(
                                        parameterWithName("accompanyPostId").description("동행 구인글 id")
                                ),
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
    @DisplayName("작성자는 해당 동행 구인글을 삭제할 수 있다.")
    void success_deleteAccompanyPost() throws Exception {
        // given
        Member member = ((CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()).getMember();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());
        AccompanyPost accompanyPost = accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 1)).get(0);
        List<AccompanyComment> accompanyComments = createAccompanyComment(member, 3);
        accompanyComments.stream().forEach(accompanyPost::addAccompanyComment);
        accompanyCommentRepository.saveAll(accompanyComments);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/accompany/posts/{accompanyPostId}", accompanyPost.getId())
                        .header("Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/deleteAccompanyPost",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("accompanyPostId").description("동행 구인글 id")
                        )
                ));
    }

    private List<AccompanyPost> createAccompanyPosts(Member member, int size) throws Exception {
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
                    .totalPeople(2L)
                    .images(createImageUrls(2)).build());
        }

        return accompanyPosts;
    }

    private List<MultipartFile> createMockMultipartFiles(int size) throws Exception {
        List<MultipartFile> images = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            images.add(new MockMultipartFile("images", "김영한.JPG",
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    new FileInputStream("src/test/resources/김영한.JPG")));
        }

        return images;
    }

    private List<String> createImageUrls(int size) {
        List<String> imageUrls = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            imageUrls.add(defaultImageUrl);
        }

        return imageUrls;
    }

    private List<AccompanyComment> createAccompanyComment(Member member, int size) {
        List<AccompanyComment> accompanyComments = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            accompanyComments.add(
                    AccompanyComment.builder().member(member).content("가는 길만 동행해도 괜찮을까요!?")
                            .build());
        }

        return accompanyComments;
    }
}