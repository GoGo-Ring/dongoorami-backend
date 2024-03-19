package com.gogoring.dongoorami.message.application;

import com.gogoring.dongoorami.message.dto.request.MessageRequest;

public interface MessageService {

    Long createMessage(MessageRequest messageRequest, Long currentMemberId);
}
