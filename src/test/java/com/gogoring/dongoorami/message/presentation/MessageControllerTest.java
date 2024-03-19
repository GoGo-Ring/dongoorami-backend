package com.gogoring.dongoorami.message.presentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogoring.dongoorami.global.customMockUser.WithCustomMockUser;
import com.gogoring.dongoorami.global.jwt.TokenProvider;
import com.gogoring.dongoorami.member.MemberDataFactory;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import com.gogoring.dongoorami.message.dto.request.MessageRequest;
import com.gogoring.dongoorami.message.repository.MessageRepository;
import java.util.Arrays;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
class MessageControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        messageRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @WithCustomMockUser
    @DisplayName("쪽지를 전송할 수 있다.")
    void success_createMessage() throws Exception {
        // given
        Member sender = MemberDataFactory.createLoginMember();
        Member receiver = MemberDataFactory.createMember();
        memberRepository.saveAll(Arrays.asList(sender, receiver));
        String accessToken = tokenProvider.createAccessToken(sender.getProviderId(),
                sender.getRoles());
        MessageRequest messageRequest = new MessageRequest(receiver.getId(), "안녕하세요~~");

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/messages")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(messageRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(document("{ClassName}/createMessage",
                        preprocessRequest(prettyPrint()),
                        requestFields(
                                fieldWithPath("receiverId").type(JsonFieldType.NUMBER)
                                        .description("받는 사람 id"),
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("내용")
                        )
                ));
    }

}