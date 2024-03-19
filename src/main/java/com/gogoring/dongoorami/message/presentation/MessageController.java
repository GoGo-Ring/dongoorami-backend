package com.gogoring.dongoorami.message.presentation;

import com.gogoring.dongoorami.global.jwt.CustomUserDetails;
import com.gogoring.dongoorami.message.application.MessageService;
import com.gogoring.dongoorami.message.dto.request.MessageRequest;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<Void> createMessage(
            @Valid @RequestBody MessageRequest messageRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.created(URI.create(
                        "/api/v1/messages/" + messageService.createMessage(messageRequest,
                                customUserDetails.getId())))
                .build();
    }

}
