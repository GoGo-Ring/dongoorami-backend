package com.gogoring.dongoorami.member.presentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogoring.dongoorami.global.customMockUser.WithCustomMockUser;
import com.gogoring.dongoorami.global.jwt.TokenProvider;
import com.gogoring.dongoorami.global.util.TestDataUtil;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.dto.request.MemberLogoutAndQuitRequest;
import com.gogoring.dongoorami.member.dto.request.MemberReissueRequest;
import com.gogoring.dongoorami.member.dto.request.MemberSignUpRequest;
import com.gogoring.dongoorami.member.dto.request.MemberUpdateRequest;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import java.io.FileInputStream;
import java.time.LocalDate;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
public class MemberControllerTest {

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
        memberRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("토큰을 재발급할 수 있다.")
    void success_reissueToken() throws Exception {
        // given
        Member member = TestDataUtil.createMember();
        memberRepository.save(member);

        String refreshToken = tokenProvider.createRefreshToken(member.getProviderId());
        MemberReissueRequest memberReissueRequest = new MemberReissueRequest();
        ReflectionTestUtils.setField(memberReissueRequest, "refreshToken", refreshToken);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/members/reissue")
                        .content(objectMapper.writeValueAsString(memberReissueRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
                .andDo(document("{ClassName}/reissueToken",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING)
                                        .description("refreshToken")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").type(JsonFieldType.STRING)
                                        .description("accessToken(만료기한 1시간)"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING)
                                        .description("refreshToken(만료기한 1주)")
                        ))
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("최초 회원가입 시 기본 정보를 저장할 수 있다.")
    void success_signUp() throws Exception {
        // given
        Member member = TestDataUtil.createLoginMember();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());

        MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest();
        ReflectionTestUtils.setField(memberSignUpRequest, "nickname", "롸롸롸");
        ReflectionTestUtils.setField(memberSignUpRequest, "gender", "남자");
        ReflectionTestUtils.setField(memberSignUpRequest, "birthDate", LocalDate.of(2000, 12, 31));

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/members/signUp")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(memberSignUpRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/signUp",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING)
                                        .description("닉네임"),
                                fieldWithPath("gender").type(JsonFieldType.STRING)
                                        .description("남자/여자"),
                                fieldWithPath("birthDate").type(JsonFieldType.STRING)
                                        .description("생년월일")
                        ))
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("로그아웃을 할 수 있다.")
    void success_logout() throws Exception {
        // given
        Member member = TestDataUtil.createLoginMember();
        memberRepository.save(member);

        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());
        String refreshToken = tokenProvider.createRefreshToken(member.getProviderId());
        MemberLogoutAndQuitRequest memberLogoutAndQuitRequest = new MemberLogoutAndQuitRequest();
        ReflectionTestUtils.setField(memberLogoutAndQuitRequest, "accessToken", accessToken);
        ReflectionTestUtils.setField(memberLogoutAndQuitRequest, "refreshToken", refreshToken);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/members/logout")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(memberLogoutAndQuitRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/logout",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("accessToken").type(JsonFieldType.STRING)
                                        .description("accessToken"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING)
                                        .description("refreshToken")
                        ))
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("회원 탈퇴를 할 수 있다.")
    void success_quit() throws Exception {
        // given
        Member member = TestDataUtil.createLoginMember();
        memberRepository.save(member);

        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());
        String refreshToken = tokenProvider.createRefreshToken(member.getProviderId());
        MemberLogoutAndQuitRequest memberLogoutAndQuitRequest = new MemberLogoutAndQuitRequest();
        ReflectionTestUtils.setField(memberLogoutAndQuitRequest, "accessToken", accessToken);
        ReflectionTestUtils.setField(memberLogoutAndQuitRequest, "refreshToken", refreshToken);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/members")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(memberLogoutAndQuitRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/quit",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("accessToken").type(JsonFieldType.STRING)
                                        .description("accessToken"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING)
                                        .description("refreshToken")
                        ))
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("프로필 이미지를 수정할 수 있다.")
    void success_updateProfileImage() throws Exception {
        // given
        Member member = TestDataUtil.createLoginMember();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());

        MockMultipartFile mockMultipartFile = new MockMultipartFile("image", "김영한.JPG",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream("src/test/resources/김영한.JPG"));

        // when
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/v1/members/profile-image")
                        .file(mockMultipartFile)
                        .header("Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.profileImageUrl").isString())
                .andDo(document("{ClassName}/updateProfileImage",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING)
                                        .description("프로필 이미지 주소")
                        )
                ));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("프로필 정보를 수정할 수 있다.")
    void success_updateMember() throws Exception {
        // given
        Member member = TestDataUtil.createLoginMember();
        ReflectionTestUtils.setField(member, "gender", "남자");
        ReflectionTestUtils.setField(member, "birthDate", LocalDate.of(2000, 12, 31));
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());

        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest();
        ReflectionTestUtils.setField(memberUpdateRequest, "name", "이롸롸");
        ReflectionTestUtils.setField(memberUpdateRequest, "introduction", "안녕하세요~");

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/members")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(memberUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(memberUpdateRequest.getName()))
                .andExpect(jsonPath("$.profileImage").value(member.getProfileImage()))
                .andExpect(jsonPath("$.gender").value(member.getGender()))
                .andExpect(jsonPath("$.age").value(member.getAge()))
                .andExpect(jsonPath("$.introduction").value(memberUpdateRequest.getIntroduction()))
                .andExpect(jsonPath("$.manner").value(member.getManner()))
                .andDo(document("{ClassName}/updateMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("닉네임"),
                                fieldWithPath("introduction").type(JsonFieldType.STRING)
                                        .description("한줄 소개")
                        ),
                        responseFields(
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("이름"),
                                fieldWithPath("profileImage").type(JsonFieldType.STRING)
                                        .description("프로필 이미지 주소"),
                                fieldWithPath("gender").type(JsonFieldType.STRING)
                                        .description("남자/여자"),
                                fieldWithPath("age").type(JsonFieldType.NUMBER)
                                        .description("나이"),
                                fieldWithPath("introduction").type(JsonFieldType.STRING)
                                        .description("한줄 소개"),
                                fieldWithPath("manner").type(JsonFieldType.NUMBER)
                                        .description("매너 지수")
                        ))
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("프로필 정보를 조회할 수 있다.")
    void success_getMember() throws Exception {
        // given
        Member member = TestDataUtil.createLoginMember();
        ReflectionTestUtils.setField(member, "gender", "남자");
        ReflectionTestUtils.setField(member, "birthDate", LocalDate.of(2000, 12, 31));
        ReflectionTestUtils.setField(member, "introduction", "안녕하세요~");

        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/members")
                        .header("Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(member.getName()))
                .andExpect(jsonPath("$.profileImage").value(member.getProfileImage()))
                .andExpect(jsonPath("$.gender").value(member.getGender()))
                .andExpect(jsonPath("$.age").value(member.getAge()))
                .andExpect(jsonPath("$.introduction").value(member.getIntroduction()))
                .andExpect(jsonPath("$.manner").value(member.getManner()))
                .andDo(document("{ClassName}/getMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("이름"),
                                fieldWithPath("profileImage").type(JsonFieldType.STRING)
                                        .description("프로필 이미지 주소"),
                                fieldWithPath("gender").type(JsonFieldType.STRING)
                                        .description("남자/여자"),
                                fieldWithPath("age").type(JsonFieldType.NUMBER)
                                        .description("나이"),
                                fieldWithPath("introduction").type(JsonFieldType.STRING)
                                        .description("한줄 소개"),
                                fieldWithPath("manner").type(JsonFieldType.NUMBER)
                                        .description("매너 지수")
                        ))
                );
    }
}
