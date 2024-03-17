package com.gogoring.dongoorami.wish.presentation;

import com.gogoring.dongoorami.global.jwt.CustomUserDetails;
import com.gogoring.dongoorami.wish.application.WishService;
import com.gogoring.dongoorami.wish.dto.response.WishesGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    @PostMapping("/wishes/{accompanyPostId}")
    public ResponseEntity<Void> createWish(@PathVariable Long accompanyPostId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        wishService.createWish(accompanyPostId, customUserDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/wishes/{accompanyPostId}")
    public ResponseEntity<Void> deleteWish(@PathVariable Long accompanyPostId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        wishService.deleteWish(accompanyPostId, customUserDetails.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/wishes")
    public ResponseEntity<WishesGetResponse> getWishes(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(wishService.getWishes(cursorId, size, customUserDetails.getId()));
    }
}
