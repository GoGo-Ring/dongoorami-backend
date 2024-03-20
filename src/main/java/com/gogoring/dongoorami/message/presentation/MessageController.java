package com.gogoring.dongoorami.message.presentation;

import com.gogoring.dongoorami.global.jwt.CustomUserDetails;
import com.gogoring.dongoorami.message.application.MessageService;
import com.gogoring.dongoorami.message.dto.request.MessageRequest;
import com.gogoring.dongoorami.message.dto.response.MessagesResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping
    public ResponseEntity<MessagesResponse> getMessages(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) List<Long> receivedPartnerIds,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(messageService.getMessages(cursorId, size, receivedPartnerIds,
                customUserDetails.getId()));
    }

}
