package com.gogoring.dongoorami.accompany.presentation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost.AccompanyPurposeType;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyCommentRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostFilterRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.accompany.repository.AccompanyCommentRepository;
import com.gogoring.dongoorami.accompany.repository.AccompanyPostRepository;
import com.gogoring.dongoorami.global.customMockUser.WithCustomMockUser;
import com.gogoring.dongoorami.global.jwt.CustomUserDetails;
import com.gogoring.dongoorami.global.jwt.TokenProvider;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
        List<MockMultipartFile> images = createMockMultipartFiles(2);
        AccompanyPostRequest accompanyPostRequest = AccompanyPostRequest.builder()
                .concertName("2024 SG워너비 콘서트 : 우리의 노래")
                .concertPlace("KSPO DOME")
                .startDate(LocalDate.of(2024, 3, 22))
                .endDate(LocalDate.of(2024, 3, 22))
                .title("서울 같이 갈 울싼 사람 구합니다~~")
                .gender("여")
                .region("경상북도/경상남도")
                .content("같이 올라갈 사람 구해요~")
                .startAge(13L)
                .endAge(17L)
                .totalPeople(1L)
                .concertPlace("2024 SG워너비 콘서트 : 우리의 노래")
                .purposes(Arrays.asList("관람", "숙박"))
                .build();
        MockMultipartFile request = new MockMultipartFile("accompanyPostRequest", null,
                "application/json", objectMapper.writeValueAsString(accompanyPostRequest)
                .getBytes(StandardCharsets.UTF_8));

        // when
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/v1/accompanies/posts")
                        .file(images.get(0))
                        .file(images.get(1))
                        .file(request)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf().asHeader())
                        .header("Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(document("{ClassName}/createAccompanyPost",
                                preprocessRequest(prettyPrint()),
                                requestParts(
                                        partWithName("images").description("이미지 0개 이상"),
                                        partWithName("accompanyPostRequest").description("동행 게시글 정보")
                                ),
                                requestPartFields("accompanyPostRequest",
                                        fieldWithPath("concertName").description("공연명"),
                                        fieldWithPath("concertPlace").description("공연 장소"),
                                        fieldWithPath("startDate").description("시작 날짜"),
                                        fieldWithPath("endDate").description("종료 날짜"),
                                        fieldWithPath("gender").description("성별"),
                                        fieldWithPath("region").description("공연 지역"),
                                        fieldWithPath("content").description("내용"),
                                        fieldWithPath("startAge").description("시작 연령"),
                                        fieldWithPath("endAge").description("종료 연령"),
                                        fieldWithPath("totalPeople").description("인원 수"),
                                        fieldWithPath("title").description("제목"),
                                        fieldWithPath("purposes").description("동행 목적 1개 이상")
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
        ReflectionTestUtils.setField(member, "nickname", "김뫄뫄");
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());
        String size = "3";
        AccompanyPostFilterRequest accompanyPostFilterRequest1 = AccompanyPostFilterRequest.builder()
                .gender("여")
                .region("경상북도/경상남도")
                .startAge(13L)
                .endAge(17L)
                .totalPeople(1L)
                .concertPlace("KSPO DOME")
                .purposes(Arrays.asList("관람", "숙박"))
                .build();
        accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 3, accompanyPostFilterRequest1));

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/accompanies/posts")
                        .header("Authorization", accessToken)
                        .param("size", size)
                        .param("gender", accompanyPostFilterRequest1.getGender())
                        .param("region", accompanyPostFilterRequest1.getRegion())
                        .param("startAge", accompanyPostFilterRequest1.getStartAge().toString())
                        .param("endAge", accompanyPostFilterRequest1.getEndAge().toString())
                        .param("totalPeople",
                                accompanyPostFilterRequest1.getTotalPeople().toString())
                        .param("concertPlace", accompanyPostFilterRequest1.getConcertPlace())
                        .param("purposes", accompanyPostFilterRequest1.getPurposes().get(0))
                        .param("purposes", accompanyPostFilterRequest1.getPurposes().get(1))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getAccompanyPostsFirst",
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("size").description("요청할 동행 구인글 개수").optional(),
                                parameterWithName("gender").description("성별").optional(),
                                parameterWithName("region").description("지역").optional(),
                                parameterWithName("startAge").description("시작 나이").optional(),
                                parameterWithName("endAge").description("종료 나이").optional(),
                                parameterWithName("totalPeople").description("인원 수").optional(),
                                parameterWithName("concertPlace").description("공연 장소").optional(),
                                parameterWithName("purposes").description("동행 목적").optional()
                        ),
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
        ReflectionTestUtils.setField(member, "nickname", "김뫄뫄");
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());
        String cursorId = "17", size = "3";
        AccompanyPostFilterRequest accompanyPostFilterRequest1 = AccompanyPostFilterRequest.builder()
                .gender("여")
                .region("경상북도/경상남도")
                .startAge(13L)
                .endAge(17L)
                .totalPeople(1L)
                .concertPlace("KSPO DOME")
                .purposes(Arrays.asList("관람", "숙박"))
                .build();
        accompanyPostRepository.saveAll(
                createAccompanyPosts(member, 3, accompanyPostFilterRequest1));

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/accompanies/posts")
                        .header("Authorization", accessToken)
                        .param("cursorId", cursorId)
                        .param("size", size)
                        .param("gender", accompanyPostFilterRequest1.getGender())
                        .param("region", accompanyPostFilterRequest1.getRegion())
                        .param("startAge", accompanyPostFilterRequest1.getStartAge().toString())
                        .param("endAge", accompanyPostFilterRequest1.getEndAge().toString())
                        .param("totalPeople",
                                accompanyPostFilterRequest1.getTotalPeople().toString())
                        .param("concertPlace", accompanyPostFilterRequest1.getConcertPlace())
                        .param("purposes", accompanyPostFilterRequest1.getPurposes().get(0))
                        .param("purposes", accompanyPostFilterRequest1.getPurposes().get(1))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getAccompanyPostsAfterFirst",
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("cursorId").description("마지막으로 받은 동행 구인글 id"),
                                parameterWithName("size").description("요청할 동행 구인글 개수").optional(),
                                parameterWithName("gender").description("성별").optional(),
                                parameterWithName("region").description("지역").optional(),
                                parameterWithName("startAge").description("시작 나이").optional(),
                                parameterWithName("endAge").description("종료 나이").optional(),
                                parameterWithName("totalPeople").description("인원 수").optional(),
                                parameterWithName("concertPlace").description("공연 장소").optional(),
                                parameterWithName("purposes").description("동행 목적").optional()
                        ),
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
        ReflectionTestUtils.setField(member, "nickname", "김뫄뫄");
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
                get("/api/v1/accompanies/posts/{accompanyPostId}", accompanyPost.getId())
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
                                fieldWithPath("memberProfile.id").type(NUMBER)
                                        .description("작성자 id"),
                                fieldWithPath("memberProfile.nickname").type(STRING)
                                        .description("작성자 닉네임"),
                                fieldWithPath("memberProfile.profileImage").type(STRING)
                                        .description("작성자 프로필 이미지 url"),
                                fieldWithPath("memberProfile.gender").type(STRING)
                                        .description("작성자 성별"),
                                fieldWithPath("memberProfile.age").type(NUMBER)
                                        .description("작성자 나이"),
                                fieldWithPath("memberProfile.introduction").type(STRING)
                                        .description("작성자 소개"),
                                fieldWithPath(
                                        "memberProfile.currentMember").type(
                                        BOOLEAN).description("본인 여부"),
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
                                fieldWithPath("isWish").type(BOOLEAN).description("찜 여부"),
                                fieldWithPath("isWriter").type(BOOLEAN).description("본인 작성 여부"),
                                fieldWithPath("purposes").type(ARRAY).description("동행 목적 리스트")
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
                post("/api/v1/accompanies/comments/{accompanyPostId}", accompanyPost.getId())
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
        ReflectionTestUtils.setField(member, "nickname", "김뫄뫄");
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
                get("/api/v1/accompanies/comments/{accompanyPostId}", accompanyPost.getId())
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
                                fieldWithPath("accompanyCommentInfos[].memberProfile.id").type(
                                                NUMBER)
                                        .description("작성자 id"),
                                fieldWithPath("accompanyCommentInfos[].memberProfile.nickname").type(
                                        STRING).description("작성자 닉네임"),
                                fieldWithPath(
                                        "accompanyCommentInfos[].memberProfile.profileImage").type(
                                        STRING).description("작성자 프로필 이미지 url"),
                                fieldWithPath("accompanyCommentInfos[].memberProfile.gender").type(
                                        STRING).description("작성자 성별"),
                                fieldWithPath("accompanyCommentInfos[].memberProfile.age").type(
                                                NUMBER)
                                        .description("작성자 나이"),
                                fieldWithPath(
                                        "accompanyCommentInfos[].memberProfile.introduction").type(
                                        STRING).description("작성자 소개"),
                                fieldWithPath(
                                        "accompanyCommentInfos[].memberProfile.currentMember").type(
                                        BOOLEAN).description("본인 여부")
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
        List<MockMultipartFile> images = createMockMultipartFiles(2);
        AccompanyPostRequest accompanyPostRequest = AccompanyPostRequest.builder()
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
                .purposes(Arrays.asList(AccompanyPurposeType.ACCOMMODATION.getName(),
                        AccompanyPurposeType.TRANSPORTATION.getName()))
                .build();
        MockMultipartFile request = new MockMultipartFile("accompanyPostRequest", null,
                "application/json", objectMapper.writeValueAsString(accompanyPostRequest)
                .getBytes(StandardCharsets.UTF_8));

        // when
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/v1/accompanies/posts/{accompanyPostId}", accompanyPost.getId())
                        .file(images.get(0))
                        .file(images.get(1))
                        .file(request)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
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
                                        partWithName("images").description("이미지 0개 이상"),
                                        partWithName("accompanyPostRequest").description("동행 게시글 정보")
                                ),
                                requestPartFields("accompanyPostRequest",
                                        fieldWithPath("concertName").description("공연명"),
                                        fieldWithPath("concertPlace").description("공연 장소"),
                                        fieldWithPath("startDate").description("시작 날짜"),
                                        fieldWithPath("endDate").description("종료 날짜"),
                                        fieldWithPath("gender").description("성별"),
                                        fieldWithPath("region").description("공연 지역"),
                                        fieldWithPath("content").description("내용"),
                                        fieldWithPath("startAge").description("시작 연령"),
                                        fieldWithPath("endAge").description("종료 연령"),
                                        fieldWithPath("totalPeople").description("인원 수"),
                                        fieldWithPath("title").description("제목"),
                                        fieldWithPath("purposes").description("동행 목적 1개 이상")
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
                delete("/api/v1/accompanies/posts/{accompanyPostId}", accompanyPost.getId())
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

    @Test
    @WithCustomMockUser
    @DisplayName("지역 목록을 조회할 수 있다.")
    void success_getAccompanyPostRegions() throws Exception {
        // given
        Member member = ((CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()).getMember();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/accompanies/posts/regions")
                        .header("Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getAccompanyPostRegions",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("regions").type(ARRAY)
                                        .description("지역 리스트")
                        )
                ));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("특정 멤버 정보를 조회할 수 있다. - 본인인 경우")
    void success_getMemberProfile_given_currentMember() throws Exception {
        // given
        Member member = ((CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()).getMember();
        ReflectionTestUtils.setField(member, "nickname", "김뫄뫄");
        ReflectionTestUtils.setField(member, "gender", "여자");
        ReflectionTestUtils.setField(member, "birthDate", LocalDate.of(2001, 1, 17));
        ReflectionTestUtils.setField(member, "introduction", "안녕하세요~");
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/accompanies/profile/{memberId}", member.getId())
                        .header("Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.currentMember").value(Boolean.TRUE))
                .andDo(document("{ClassName}/getMemberProfile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberId").description("멤버 id")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER)
                                        .description("멤버 id"),
                                fieldWithPath("nickname").type(
                                        STRING).description("닉네임"),
                                fieldWithPath(
                                        "profileImage").type(
                                        STRING).description("프로필 이미지 url"),
                                fieldWithPath("gender").type(
                                        STRING).description("성별"),
                                fieldWithPath("age").type(NUMBER)
                                        .description("나이"),
                                fieldWithPath(
                                        "introduction").type(
                                        STRING).description("소개"),
                                fieldWithPath(
                                        "currentMember").type(
                                        BOOLEAN).description("본인 여부")
                        )
                ));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("특정 멤버 정보를 조회할 수 있다. - 본인이 아닌 경우")
    void success_getMemberProfile_given_notCurrentMember() throws Exception {
        // given
        Member member = ((CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()).getMember();
        ReflectionTestUtils.setField(member, "gender", "여자");
        ReflectionTestUtils.setField(member, "birthDate", LocalDate.of(2001, 1, 17));
        ReflectionTestUtils.setField(member, "introduction", "안녕하세요~");
        Member member2 = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("hajkdflajkflajdklag")
                .build();
        ReflectionTestUtils.setField(member2, "nickname", "김뭐뭐");
        ReflectionTestUtils.setField(member2, "gender", "여자");
        ReflectionTestUtils.setField(member2, "birthDate", LocalDate.of(2001, 1, 17));
        ReflectionTestUtils.setField(member2, "introduction", "안녕하세요~");
        memberRepository.save(member);
        member2 = memberRepository.save(member2);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());
        System.out.println(member.getId() + ", " + member2.getId());

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/accompanies/profile/{memberId}", member2.getId())
                        .header("Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.currentMember").value(Boolean.FALSE));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("동행 구인글의 댓글 작성자는 댓글을 수정할 수 있다.")
    void success_updateAccompanyComment() throws Exception {
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
        AccompanyCommentRequest accompanyCommentRequest = new AccompanyCommentRequest(
                "오는 길만 동행 가능할까요??");

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/accompanies/comments/{accompanyPostId}",
                        accompanyComments.get(0).getId())
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(accompanyCommentRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/updateAccompanyComment",
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
    @DisplayName("동행 구인글의 댓글 작성자는 댓글을 삭제할 수 있다.")
    void success_deleteAccompanyComment() throws Exception {
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
        AccompanyCommentRequest accompanyCommentRequest = new AccompanyCommentRequest(
                "오는 길만 동행 가능할까요??");

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/accompanies/comments/{accompanyPostId}",
                        accompanyComments.get(0).getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/deleteAccompanyComment",
                        preprocessRequest(prettyPrint()),
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
                    .region("수도권(경기, 인천 포함)")
                    .content("같이 올라갈 사람 구해요~")
                    .startAge(23L)
                    .endAge(37L)
                    .totalPeople(2L)
                    .images(createImageUrls(2))
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

    private List<MockMultipartFile> createMockMultipartFiles(int size) throws Exception {
        List<MockMultipartFile> images = new ArrayList<>();
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