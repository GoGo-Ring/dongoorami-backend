package com.gogoring.dongoorami.message.presentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
import com.gogoring.dongoorami.global.customMockUser.WithCustomMockUser;
import com.gogoring.dongoorami.global.jwt.TokenProvider;
import com.gogoring.dongoorami.member.MemberDataFactory;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import com.gogoring.dongoorami.message.MessageDataFactory;
import com.gogoring.dongoorami.message.dto.request.MessageRequest;
import com.gogoring.dongoorami.message.repository.MessageRepository;
import java.util.Arrays;
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
                                fieldWithPath("partnerId").type(JsonFieldType.NUMBER)
                                        .description("쪽지 상대 id"),
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("내용")
                        )
                ));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("사용자와 대화 중인 쪽지 목록을 조회할 수 있다. - 최초 요청")
    void success_getMessagesFirst() throws Exception {
        // given
        List<Member> members = memberRepository.saveAll(
                Arrays.asList(MemberDataFactory.createLoginMember(),
                        MemberDataFactory.createMember(),
                        MemberDataFactory.createMember(), MemberDataFactory.createMember()));
        Member member1 = members.get(0), member2 = members.get(1), member3 = members.get(
                2), member4 = members.get(3);
        String accessToken = tokenProvider.createAccessToken(member1.getProviderId(),
                member1.getRoles());
        String size = "10";

        messageRepository.saveAll(MessageDataFactory.createMessages(member1, member2, 5));
        messageRepository.saveAll(MessageDataFactory.createMessages(member2, member1, 5));

        messageRepository.saveAll(MessageDataFactory.createMessages(member1, member3, 5));
        messageRepository.saveAll(MessageDataFactory.createMessages(member3, member1, 5));

        messageRepository.saveAll(MessageDataFactory.createMessages(member1, member4, 5));
        messageRepository.saveAll(MessageDataFactory.createMessages(member4, member1, 5));

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/messages")
                        .header("Authorization", accessToken)
                        .param("size", size)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getMessagesFirst",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("size").description("요청할 쪽지 목록 개수(기본 10개)")
                                        .optional()
                        ),
                        responseFields(
                                fieldWithPath("hasNext").type(BOOLEAN)
                                        .description("다음 쪽지 목록 존재 여부"),
                                fieldWithPath("messageResponses").type(ARRAY)
                                        .description("쪽지 목록"),
                                fieldWithPath("messageResponses.[].id").type(NUMBER)
                                        .description("가장 최근 쪽지 id"),
                                fieldWithPath("messageResponses.[].partner.id").type(NUMBER)
                                        .description("쪽지 상대 id"),
                                fieldWithPath("messageResponses.[].partner.nickname").type(STRING)
                                        .description("쪽지 상대 닉네임"),
                                fieldWithPath("messageResponses.[].partner.profileImage").type(
                                                STRING)
                                        .description("쪽지 상대 프로필 이미지 url"),
                                fieldWithPath("messageResponses.[].partner.gender").type(STRING)
                                        .description("쪽지 상대 성별"),
                                fieldWithPath("messageResponses.[].partner.age").type(NUMBER)
                                        .description("쪽지 상대 나이"),
                                fieldWithPath("messageResponses.[].partner.introduction").type(
                                                STRING)
                                        .description("쪽지 상대 소개"),
                                fieldWithPath(
                                        "messageResponses.[].partner.currentMember").type(
                                        BOOLEAN).description("본인 여부(상대방에 대한 정보라 무조건 false)"),
                                fieldWithPath(
                                        "messageResponses.[].partner.manner").type(
                                        NUMBER).description("매너 지수"),
                                fieldWithPath("messageResponses.[].content").type(STRING)
                                        .description("가장 최근 쪽지 내용"),
                                fieldWithPath("messageResponses.[].createdAt").type(STRING)
                                        .description("가장 최근 쪽지 작성 일시"),
                                fieldWithPath("messageResponses.[].hasUnRead").type(BOOLEAN)
                                        .description("읽지 않은 쪽지 존재 여부(존재 true, 미존재 false)"),
                                fieldWithPath("messageResponses.[].myMessage").type(BOOLEAN)
                                        .description("본인이 보낸 쪽지 여부")
                        ))
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("사용자와 대화 중인 쪽지 목록을 조회할 수 있다. - 이후 요청")
    void success_getMessagesAfterFirst() throws Exception {
        // given
        List<Member> members = memberRepository.saveAll(
                Arrays.asList(MemberDataFactory.createLoginMember(),
                        MemberDataFactory.createMember(),
                        MemberDataFactory.createMember(), MemberDataFactory.createMember()));
        Member member1 = members.get(0), member2 = members.get(1), member3 = members.get(
                2), member4 = members.get(3);
        String accessToken = tokenProvider.createAccessToken(member1.getProviderId(),
                member1.getRoles());
        String size = "10", cursorId;

        messageRepository.saveAll(MessageDataFactory.createMessages(member1, member2, 5));
        messageRepository.saveAll(MessageDataFactory.createMessages(member2, member1, 5));

        messageRepository.saveAll(MessageDataFactory.createMessages(member1, member3, 5));
        messageRepository.saveAll(MessageDataFactory.createMessages(member3, member1, 5));

        messageRepository.saveAll(MessageDataFactory.createMessages(member1, member4, 5));
        messageRepository.saveAll(MessageDataFactory.createMessages(member4, member1, 5));
        Long latestMessageId = messageRepository.save(
                MessageDataFactory.createMessages(member3, member1, 1).get(0)).getId();
        cursorId = String.valueOf(latestMessageId + 1);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/messages")
                        .header("Authorization", accessToken)
                        .param("cursorId", cursorId)
                        .param("size", size)
                        .param("receivedPartnerIds", String.valueOf(member3.getId()),
                                String.valueOf(member4.getId()))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getMessagesAfterFirst",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("cursorId").description("마지막으로 받은 쪽지 id")
                                        .optional(),
                                parameterWithName("size").description("요청할 쪽지 목록 개수(기본 10개)")
                                        .optional(),
                                parameterWithName("receivedPartnerIds").description(
                                        "지금까지 받은 쪽지 상대 id 리스트").optional()
                        ),
                        responseFields(
                                fieldWithPath("hasNext").type(BOOLEAN)
                                        .description("다음 쪽지 목록 존재 여부"),
                                fieldWithPath("messageResponses").type(ARRAY)
                                        .description("쪽지 목록"),
                                fieldWithPath("messageResponses.[].id").type(NUMBER)
                                        .description("가장 최근 쪽지 id"),
                                fieldWithPath("messageResponses.[].partner.id").type(NUMBER)
                                        .description("쪽지 상대 id"),
                                fieldWithPath("messageResponses.[].partner.nickname").type(STRING)
                                        .description("쪽지 상대 닉네임"),
                                fieldWithPath("messageResponses.[].partner.profileImage").type(
                                                STRING)
                                        .description("쪽지 상대 프로필 이미지 url"),
                                fieldWithPath("messageResponses.[].partner.gender").type(STRING)
                                        .description("쪽지 상대 성별"),
                                fieldWithPath("messageResponses.[].partner.age").type(NUMBER)
                                        .description("쪽지 상대 나이"),
                                fieldWithPath("messageResponses.[].partner.introduction").type(
                                                STRING)
                                        .description("쪽지 상대 소개"),
                                fieldWithPath(
                                        "messageResponses.[].partner.currentMember").type(
                                        BOOLEAN).description("본인 여부(상대에 대한 정보라 무조건 false)"),
                                fieldWithPath(
                                        "messageResponses.[].partner.manner").type(
                                        NUMBER).description("쪽지 상대 매너 지수"),
                                fieldWithPath("messageResponses.[].content").type(STRING)
                                        .description("가장 최근 쪽지 내용"),
                                fieldWithPath("messageResponses.[].createdAt").type(STRING)
                                        .description("가장 최근 쪽지 작성 일시"),
                                fieldWithPath("messageResponses.[].hasUnRead").type(BOOLEAN)
                                        .description("읽지 않은 쪽지 존재 여부(존재 true, 미존재 false)"),
                                fieldWithPath("messageResponses.[].myMessage").type(BOOLEAN)
                                        .description("본인이 보낸 쪽지 여부")
                        ))
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("사용자와 대화 중인 쪽지 목록을 조회할 수 있다. - 최초 요청")
    void success_getMessagesWithPartnerFirst() throws Exception {
        // given
        List<Member> members = memberRepository.saveAll(
                Arrays.asList(MemberDataFactory.createLoginMember(),
                        MemberDataFactory.createMember(),
                        MemberDataFactory.createMember(), MemberDataFactory.createMember()));
        Member member1 = members.get(0), member2 = members.get(1);
        String accessToken = tokenProvider.createAccessToken(member1.getProviderId(),
                member1.getRoles());
        String size = "10";

        messageRepository.saveAll(MessageDataFactory.createMessages(member1, member2, 5));
        messageRepository.saveAll(MessageDataFactory.createMessages(member2, member1, 5));

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/messages/{partnerId}", member2.getId())
                        .header("Authorization", accessToken)
                        .param("size", size)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getMessagesWithPartnerFirst",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("partnerId").description("쪽지 상대 id")
                        ),
                        queryParameters(
                                parameterWithName("size").description("요청할 쪽지 개수(기본 10개)")
                                        .optional()
                        ),
                        responseFields(
                                fieldWithPath("hasNext").type(BOOLEAN)
                                        .description("다음 쪽지 존재 여부"),
                                fieldWithPath("messageResponses").type(ARRAY)
                                        .description("쪽지 목록"),
                                fieldWithPath("messageResponses.[].id").type(NUMBER)
                                        .description("쪽지 id"),
                                fieldWithPath("messageResponses.[].partner.id").type(NUMBER)
                                        .description("쪽지 상대 id"),
                                fieldWithPath("messageResponses.[].partner.nickname").type(STRING)
                                        .description("쪽지 상대 닉네임"),
                                fieldWithPath("messageResponses.[].partner.profileImage").type(
                                                STRING)
                                        .description("쪽지 상대 프로필 이미지 url"),
                                fieldWithPath("messageResponses.[].partner.gender").type(STRING)
                                        .description("쪽지 상대 성별"),
                                fieldWithPath("messageResponses.[].partner.age").type(NUMBER)
                                        .description("쪽지 상대 나이"),
                                fieldWithPath("messageResponses.[].partner.introduction").type(
                                                STRING)
                                        .description("쪽지 상대 소개"),
                                fieldWithPath(
                                        "messageResponses.[].partner.currentMember").type(
                                        BOOLEAN).description("본인 여부(상대에 대한 정보라 무조건 false)"),
                                fieldWithPath(
                                        "messageResponses.[].partner.manner").type(
                                        NUMBER).description("매너 지수"),
                                fieldWithPath("messageResponses.[].content").type(STRING)
                                        .description("쪽지 내용"),
                                fieldWithPath("messageResponses.[].createdAt").type(STRING)
                                        .description("쪽지 작성 일시"),
                                fieldWithPath("messageResponses.[].hasUnRead").type(BOOLEAN)
                                        .description("쪽지 읽음 여부(읽음 false, 안읽음 true)"),
                                fieldWithPath("messageResponses.[].myMessage").type(BOOLEAN)
                                        .description("본인이 보낸 쪽지 여부")
                        ))
                );
    }

    @Test
    @WithCustomMockUser
    @DisplayName("사용자와 대화 중인 쪽지 목록을 조회할 수 있다. - 이후 요청")
    void success_getMessagesWithPartnerAfterFirst() throws Exception {
        // given
        List<Member> members = memberRepository.saveAll(
                Arrays.asList(MemberDataFactory.createLoginMember(),
                        MemberDataFactory.createMember(),
                        MemberDataFactory.createMember(), MemberDataFactory.createMember()));
        Member member1 = members.get(0), member2 = members.get(1);
        String accessToken = tokenProvider.createAccessToken(member1.getProviderId(),
                member1.getRoles());
        String size = "10", cursorId;

        messageRepository.saveAll(MessageDataFactory.createMessages(member1, member2, 5));
        messageRepository.saveAll(MessageDataFactory.createMessages(member2, member1, 5));
        Long latestMessageId = messageRepository.save(
                MessageDataFactory.createMessages(member1, member2, 1).get(0)).getId();
        cursorId = String.valueOf(latestMessageId + 1);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/messages/{partnerId}", member2.getId())
                        .header("Authorization", accessToken)
                        .param("cursorId", cursorId)
                        .param("size", size)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("{ClassName}/getMessagesWithPartnerAfterFirst",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("partnerId").description("쪽지 상대 id")
                        ),
                        queryParameters(
                                parameterWithName("cursorId").description("마지막으로 받은 쪽지 id")
                                        .optional(),
                                parameterWithName("size").description("요청할 쪽지 개수(기본 10개)")
                                        .optional()
                        ),
                        responseFields(
                                fieldWithPath("hasNext").type(BOOLEAN)
                                        .description("다음 쪽지 존재 여부"),
                                fieldWithPath("messageResponses").type(ARRAY)
                                        .description("쪽지 목록"),
                                fieldWithPath("messageResponses.[].id").type(NUMBER)
                                        .description("쪽지 id"),
                                fieldWithPath("messageResponses.[].partner.id").type(NUMBER)
                                        .description("쪽지 상대 id"),
                                fieldWithPath("messageResponses.[].partner.nickname").type(STRING)
                                        .description("쪽지 상대 닉네임"),
                                fieldWithPath("messageResponses.[].partner.profileImage").type(
                                                STRING)
                                        .description("쪽지 상대 프로필 이미지 url"),
                                fieldWithPath("messageResponses.[].partner.gender").type(STRING)
                                        .description("쪽지 상대 성별"),
                                fieldWithPath("messageResponses.[].partner.age").type(NUMBER)
                                        .description("쪽지 상대 나이"),
                                fieldWithPath("messageResponses.[].partner.introduction").type(
                                                STRING)
                                        .description("쪽지 상대 소개"),
                                fieldWithPath(
                                        "messageResponses.[].partner.currentMember").type(
                                        BOOLEAN).description("본인 여부(상대에 대한 정보라 무조건 false)"),
                                fieldWithPath(
                                        "messageResponses.[].partner.manner").type(
                                        NUMBER).description("매너 지수"),
                                fieldWithPath("messageResponses.[].content").type(STRING)
                                        .description("쪽지 내용"),
                                fieldWithPath("messageResponses.[].createdAt").type(STRING)
                                        .description("쪽지 작성 일시"),
                                fieldWithPath("messageResponses.[].hasUnRead").type(BOOLEAN)
                                        .description("쪽지 읽음 여부(읽음 false, 안읽음 true)"),
                                fieldWithPath("messageResponses.[].myMessage").type(BOOLEAN)
                                        .description("본인이 보낸 쪽지 여부")
                        ))
                );
    }
}