package com.gogoring.dongoorami.message.application;

import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.exception.MemberErrorCode;
import com.gogoring.dongoorami.member.exception.MemberNotFoundException;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import com.gogoring.dongoorami.message.domain.Message;
import com.gogoring.dongoorami.message.dto.request.MessageRequest;
import com.gogoring.dongoorami.message.dto.response.MessageResponse;
import com.gogoring.dongoorami.message.dto.response.MessagesResponse;
import com.gogoring.dongoorami.message.repository.MessageRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;

    @Override
    public Long createMessage(MessageRequest messageRequest, Long currentMemberId) {
        Member sender = memberRepository.findByIdAndIsActivatedIsTrue(currentMemberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        Member partner = memberRepository.findByIdAndIsActivatedIsTrue(
                        messageRequest.getPartnerId())
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        messageRepository.save(messageRequest.toEntity(sender, partner));

        return messageRequest.getPartnerId();
    }

    @Override
    public MessagesResponse getMessages(Long cursorId, int size, List<Long> receivedPartnerIds,
            Long currentMemberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(currentMemberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        Slice<Message> messages = messageRepository.findLatestMessages(member, cursorId, size,
                receivedPartnerIds);

        return new MessagesResponse(messages.hasNext(),
                messages.getContent().stream().map(message -> {
                    Member partner = message.getSender().equals(member) ? message.getReceiver()
                            : message.getSender();
                    boolean hasUnRead = messageRepository.existsBySenderAndReceiverAndIsReadFalseAndIsActivatedIsTrue(
                            partner, member);
                    return MessageResponse.of(message, partner, member, hasUnRead);
                }).toList());
    }

    @Transactional
    @Override
    public MessagesResponse getMessagesWithPartner(Long cursorId, int size, Long partnerId,
            Long currentMemberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(currentMemberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        Member partner = memberRepository.findByIdAndIsActivatedIsTrue(partnerId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        Slice<Message> messagesWithPartner = messageRepository.findMessagesWithPartner(member,
                partner, cursorId, size);
        messagesWithPartner.stream().filter(message -> message.getReceiver().equals(member))
                .forEach(Message::updateIsRead);

        return new MessagesResponse(messagesWithPartner.hasNext(),
                messagesWithPartner.getContent().stream()
                        .map(message -> MessageResponse.of(message, member)).collect(
                                Collectors.toList()));
    }

}
