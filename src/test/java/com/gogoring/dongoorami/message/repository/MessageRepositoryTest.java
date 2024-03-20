package com.gogoring.dongoorami.message.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.gogoring.dongoorami.global.config.QueryDslConfig;
import com.gogoring.dongoorami.member.MemberDataFactory;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import com.gogoring.dongoorami.message.MessageDataFactory;
import com.gogoring.dongoorami.message.domain.Message;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;

@Import(QueryDslConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MessageRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MessageRepository messageRepository;

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
    @DisplayName("이미 받은 상대 id를 제외하고 주어진 메시지 id 이후에 생성된 사용자와 대화 중인 쪽지 목록을 최신 순으로 조회할 수 있다.")
    void success_findAllByOrderByIdDesc() {
        // given
        List<Member> members = memberRepository.saveAll(
                Arrays.asList(MemberDataFactory.createMember(), MemberDataFactory.createMember(),
                        MemberDataFactory.createMember()));
        Member member1 = members.get(0), member2 = members.get(1), member3 = members.get(2);

        messageRepository.saveAll(MessageDataFactory.createMessages(member1, member2, 5));
        messageRepository.saveAll(MessageDataFactory.createMessages(member2, member1, 5));
        Long latestMessageWithMember2Id = messageRepository.save(
                MessageDataFactory.createMessages(member1, member2, 1).get(0)).getId();

        messageRepository.saveAll(MessageDataFactory.createMessages(member2, member3, 5));
        messageRepository.saveAll(MessageDataFactory.createMessages(member3, member2, 5));

        messageRepository.saveAll(MessageDataFactory.createMessages(member1, member3, 5));
        messageRepository.saveAll(MessageDataFactory.createMessages(member3, member1, 5));
        Long latestMessageWithMember3Id = messageRepository.save(
                MessageDataFactory.createMessages(member3, member1, 1).get(0)).getId();
        Long cursorId = latestMessageWithMember3Id + 1;

        // when
        Slice<Message> messageSlice1 = messageRepository.findLatestMessages(member1, cursorId, 5,
                List.of());
        Slice<Message> messageSlice2 = messageRepository.findLatestMessages(member1, cursorId, 5,
                Collections.singletonList(member2.getId()));
        Slice<Message> messageSlice3 = messageRepository.findLatestMessages(member1, cursorId, 5,
                Arrays.asList(member2.getId(), member3.getId()));

        // then
        assertThat(messageSlice1.getContent().size(), equalTo(2));
        assertThat(messageSlice1.getContent().get(0).getId(), equalTo(latestMessageWithMember3Id));
        assertThat(messageSlice1.getContent().get(1).getId(), equalTo(latestMessageWithMember2Id));
        assertThat(messageSlice2.getContent().size(), equalTo(1));
        assertThat(messageSlice3.getContent().size(), equalTo(0));
    }

    @Test
    @DisplayName("특정 수신자는 특정 전송자가 자신에게 보낸 메시지 중 읽지 않은 것이 있는지 조회할 수 있다.")
    void success_existsBySenderAndReceiverAndIsReadFalseAndIsActivatedIsTrue() {
        // given
        List<Member> members = memberRepository.saveAll(
                Arrays.asList(MemberDataFactory.createMember(), MemberDataFactory.createMember(),
                        MemberDataFactory.createMember()));
        Member member1 = members.get(0), member2 = members.get(1), member3 = members.get(2);

        messageRepository.saveAll(MessageDataFactory.createMessages(member1, member2, 5));
        messageRepository.saveAll(MessageDataFactory.createMessages(member2, member1, 5));
        Message message = MessageDataFactory.createMessages(member3, member1, 1).get(0);
        message.updateIsRead();
        messageRepository.save(message);

        // when
        boolean member1hasUnReadMember2Message = messageRepository.existsBySenderAndReceiverAndIsReadFalseAndIsActivatedIsTrue(
                member2, member1);
        boolean member1hasUnReadMember3Message = messageRepository.existsBySenderAndReceiverAndIsReadFalseAndIsActivatedIsTrue(
                member3, member1);

        // then
        assertThat(member1hasUnReadMember2Message, equalTo(true));
        assertThat(member1hasUnReadMember3Message, equalTo(false));
    }
}