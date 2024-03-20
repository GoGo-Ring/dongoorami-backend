package com.gogoring.dongoorami.message;

import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.message.domain.Message;
import java.util.ArrayList;
import java.util.List;

public class MessageDataFactory {

    public static List<Message> createMessages(Member sender, Member receiver, int size) {
        List<Message> messages = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            messages.add(Message.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .content("안녕하시렵니까")
                    .build());
        }

        return messages;
    }

}
