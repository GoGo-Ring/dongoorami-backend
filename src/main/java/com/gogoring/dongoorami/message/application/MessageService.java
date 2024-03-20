package com.gogoring.dongoorami.message.application;

import com.gogoring.dongoorami.message.dto.request.MessageRequest;
import com.gogoring.dongoorami.message.dto.response.MessagesResponse;
import java.util.List;

public interface MessageService {

    Long createMessage(MessageRequest messageRequest, Long currentMemberId);

    MessagesResponse getMessages(Long cursorId, int size, List<Long> receivedPartnerIds,
            Long currentMemberId);

    MessagesResponse getMessagesWithPartner(Long cursorId, int size, Long partnerId,
            Long currentMemberId);
}
