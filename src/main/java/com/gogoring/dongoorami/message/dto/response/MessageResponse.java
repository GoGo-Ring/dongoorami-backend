package com.gogoring.dongoorami.message.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogoring.dongoorami.accompany.dto.response.MemberProfile;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.message.domain.Message;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MessageResponse {

    private final Long id;
    private final MemberProfile partner;
    private final String content;

    @JsonFormat(pattern = "yyyy.MM.dd.hh:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;
    private final boolean hasUnRead;
    private final boolean isMyMessage;

    public static MessageResponse of(Message message, Member partner, Member member,
            boolean hasUnRead) {
        return MessageResponse.builder()
                .id(message.getId())
                .partner(MemberProfile.of(partner, member.getId()))
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .hasUnRead(hasUnRead)
                .isMyMessage(message.getSender().equals(member))
                .build();
    }

    public static MessageResponse of(Message message, Member member) {
        Member partner =
                !message.getSender().equals(member) ? message.getSender() : message.getReceiver();
        return MessageResponse.builder()
                .id(message.getId())
                .partner(MemberProfile.of(partner, member.getId()))
                .content(message.getContent())
                .createdAt(message.getUpdatedAt())
                .hasUnRead(!message.isRead())
                .isMyMessage(message.getSender().equals(member))
                .build();
    }
}
