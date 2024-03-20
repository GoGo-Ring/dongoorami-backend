package com.gogoring.dongoorami.message.repository;

import static com.querydsl.jpa.JPAExpressions.select;

import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.message.domain.Message;
import com.gogoring.dongoorami.message.domain.QMessage;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class MessageCustomRepositoryImpl implements MessageCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QMessage message = QMessage.message;

    @Override
    public Slice<Message> findLatestMessages(Member member, Long cursorId, int size,
            List<Long> receivedPartnerIds) {
        List<Message> messages = jpaQueryFactory.selectFrom(message)
                .where(isMemberParticipatingAndPartnerIdIsNotInPartnerIds(member,
                        receivedPartnerIds)
                        .and(message.id.in(
                                select(message.id.max())
                                        .from(message)
                                        .groupBy(message.receiver, message.sender)
                        ))
                        .and(lessThanCursorId(cursorId))
                ).orderBy(message.id.desc()).limit(size).fetch();
        boolean hasNext = false;
        if (!messages.isEmpty()) {
            Long lastIdInResult = messages.get(messages.size() - 1).getId();
            hasNext = isExistByIdLessThan(lastIdInResult, member,
                    getPartnerIdsFromMessages(messages, member, receivedPartnerIds));
        }

        return new SliceImpl<>(findLatestMessageInSameConversation(messages), Pageable.ofSize(size),
                hasNext);
    }

    private BooleanExpression isMemberParticipatingAndPartnerIdIsNotInReceivedPartnerIds(
            Member member, List<Long> receivedPartnerIds) {
        if (receivedPartnerIds == null) {

    private BooleanExpression isMemberParticipatingAndPartnerIdIsNotInPartnerIds(
            Member member, List<Long> partnerIds) {
        if (partnerIds == null) {
            return message.sender.eq(member).or(message.receiver.eq(member));
        }
        return message.sender.eq(member).and(message.receiver.id.notIn(partnerIds))
                .or(message.receiver.eq(member).and(message.sender.id.notIn(partnerIds)));
    }

    private BooleanExpression lessThanCursorId(Long cursorId) {
        return cursorId != null ? message.id.lt(cursorId) : null;
    }

    private boolean isExistByIdLessThan(Long id, Member member, List<Long> partnerIds) {
        return jpaQueryFactory.selectFrom(message)
                .where(isMemberParticipatingAndPartnerIdIsNotInPartnerIds(member,
                        partnerIds).and(message.id.lt(id)
                ).and(message.isActivated.eq(true)))
                .fetchFirst() != null;
    }

                .fetchFirst() != null;
    }

    /**
     * senderId가 a이고, receiverId가 b인 경우와 senderId가 b이고, receiverId가 a인 경우 모두 같은 대화에 속하므로 둘 중 더 최근의
     * 메시지를 추출
     */
    private List<Message> findLatestMessageInSameConversation(List<Message> messages) {
        Map<String, Message> messageMap = new HashMap<>();
        for (Message message : messages) {
            ArrayList<Long> memberIds = new ArrayList<>(
                    Arrays.asList(message.getSender().getId(), message.getReceiver().getId()));
            Collections.sort(memberIds);
            String key = memberIds.get(0) + ", " + memberIds.get(1);
            if (messageMap.containsKey(key)) {
                if (messageMap.get(key).getId() < message.getId()) {
                    messageMap.put(key, message);
                }
            } else {
                messageMap.put(key, message);
            }
        }
        List<Message> latestMessageInSameConversation = new ArrayList<>(messageMap.values());
        Comparator<Message> messageIdDescComparator = Comparator.comparingLong(Message::getId)
                .reversed();
        latestMessageInSameConversation.sort(messageIdDescComparator);

        return latestMessageInSameConversation;
    }
    private List<Long> getPartnerIdsFromMessages(List<Message> messages, Member member,
            List<Long> receivedPartnerIds) {
        List<Long> partnerIds = new ArrayList<>();
        if (receivedPartnerIds != null) {
            partnerIds.addAll(receivedPartnerIds);
        }

        messages.stream()
                .filter(message -> !message.getReceiver().getId().equals(member.getId()))
                .map(message -> message.getReceiver().getId())
                .forEach(partnerIds::add);

        messages.stream()
                .filter(message -> !message.getSender().getId().equals(member.getId()))
                .map(message -> message.getReceiver().getId())
                .forEach(partnerIds::add);

        return partnerIds;
    }
}
