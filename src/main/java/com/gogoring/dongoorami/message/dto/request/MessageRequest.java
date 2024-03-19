package com.gogoring.dongoorami.message.dto.request;

import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.message.domain.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class MessageRequest {

    @NotNull(message = "receiverId는 공백일 수 없습니다.")
    private Long receiverId;

    @NotBlank(message = "content는 공백일 수 없습니다.")
    private String content;

    public Message toEntity(Member sender, Member receiver) {
        return Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .build();
    }
}