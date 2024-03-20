package com.gogoring.dongoorami.message.application;

import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.exception.MemberErrorCode;
import com.gogoring.dongoorami.member.exception.MemberNotFoundException;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import com.gogoring.dongoorami.message.dto.request.MessageRequest;
import com.gogoring.dongoorami.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
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
        Member receiver = memberRepository.findByIdAndIsActivatedIsTrue(
                        messageRequest.getReceiverId())
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        messageRepository.save(messageRequest.toEntity(sender, receiver));

        return messageRequest.getReceiverId();
    }
}