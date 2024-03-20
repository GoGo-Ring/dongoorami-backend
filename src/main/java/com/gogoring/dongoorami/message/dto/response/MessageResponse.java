package com.gogoring.dongoorami.message.dto.response;

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
    private final LocalDateTime createdAt;
    private final boolean hasUnRead;

    public static MessageResponse of(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .partner(MemberProfile.of(message.getSender(), message.getSender().getId()))
                .content(message.getContent())
                .createdAt(message.getUpdatedAt())
                .hasUnRead(message.isRead())
                .build();
    }

    public static MessageResponse of(Message message, Member partner, Member member,
            boolean hasUnRead) {
        return MessageResponse.builder()
                .id(message.getId())
                .partner(MemberProfile.of(partner, member.getId()))
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .hasUnRead(hasUnRead)
                .build();
    }
}
