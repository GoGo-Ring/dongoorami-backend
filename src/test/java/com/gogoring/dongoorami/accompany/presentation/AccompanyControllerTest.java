package com.gogoring.dongoorami.accompany.presentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.global.customMockUser.WithCustomMockUser;
import com.gogoring.dongoorami.global.jwt.TokenProvider;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import java.time.LocalDate;
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
    private TokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
}