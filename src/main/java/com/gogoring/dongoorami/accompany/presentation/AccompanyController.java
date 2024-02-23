package com.gogoring.dongoorami.accompany.presentation;

import com.gogoring.dongoorami.accompany.application.AccompanyService;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.global.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accompany")
@RequiredArgsConstructor
public class AccompanyController {

    private final AccompanyService accompanyService;

    @PostMapping("/posts")
    public ResponseEntity<Void> createAccompanyPost(
            @Valid @RequestBody AccompanyPostRequest accompanyPostRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        accompanyService.createAccompanyPost(accompanyPostRequest, customUserDetails.getId());
        return ResponseEntity.ok().build();
    }

}
