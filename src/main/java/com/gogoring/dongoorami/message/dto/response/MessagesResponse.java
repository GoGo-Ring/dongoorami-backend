package com.gogoring.dongoorami.message.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessagesResponse {

    private Boolean hasNext;
    private List<MessageResponse> messageResponses;
}
