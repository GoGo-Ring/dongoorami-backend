package com.gogoring.dongoorami.member.presentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gogoring.dongoorami.global.customMockUser.WithCustomMockUser;
import com.gogoring.dongoorami.global.jwt.TokenProvider;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.dto.request.MemberLogoutAndQuitRequest;
import com.gogoring.dongoorami.member.dto.request.MemberReissueRequest;
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
import org.springframework.security.core.context.SecurityContextHolder;
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
        Member member = Member.builder()
                .name("김뫄뫄")
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
        memberRepository.save(member);

        String refreshToken = tokenProvider.createRefreshToken(member.getProviderId());
        MemberReissueRequest memberReissueRequest = new MemberReissueRequest();
        ReflectionTestUtils.setField(memberReissueRequest, "refreshToken", refreshToken);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/members/reissue")
                        .content(new ObjectMapper().writeValueAsString(memberReissueRequest))
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
    @DisplayName("로그아웃을 할 수 있다.")
    void success_logout() throws Exception {
        // given
        Member member = Member.builder()
                .name("김뫄뫄")
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
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
                        .content(new ObjectMapper().writeValueAsString(memberLogoutAndQuitRequest))
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
        Member member = (Member) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
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
                        .content(new ObjectMapper().writeValueAsString(memberLogoutAndQuitRequest))
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
        Member member = (Member) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
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
        Member member = (Member) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        memberRepository.save(member);
        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());

        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest();
        ReflectionTestUtils.setField(memberUpdateRequest, "gender", "남자");
        ReflectionTestUtils.setField(memberUpdateRequest, "birthDate", LocalDate.of(2000, 12, 31));
        ReflectionTestUtils.setField(memberUpdateRequest, "introduction", "안녕하세요~");

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/members")
                        .header("Authorization", accessToken)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule())
                                .writeValueAsString(memberUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(member.getName()))
                .andExpect(jsonPath("$.profileImage").value(member.getProfileImage()))
                .andExpect(jsonPath("$.gender").value(memberUpdateRequest.getGender()))
                .andExpect(jsonPath("$.age").value(
                        LocalDate.now().getYear() - memberUpdateRequest.getBirthDate().getYear()
                                + 1))
                .andExpect(jsonPath("$.introduction").value(memberUpdateRequest.getIntroduction()))
                .andDo(document("{ClassName}/updateMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("gender").type(JsonFieldType.STRING)
                                        .description("남자/여자"),
                                fieldWithPath("birthDate").type("LocalDate")
                                        .description("생년월일"),
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
                                        .description("한줄 소개")
                        ))
                );
    }
}
