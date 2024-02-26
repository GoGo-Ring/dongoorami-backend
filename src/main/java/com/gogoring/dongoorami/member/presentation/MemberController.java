package com.gogoring.dongoorami.member.presentation;

import com.gogoring.dongoorami.global.jwt.CustomUserDetails;
import com.gogoring.dongoorami.member.application.MemberService;
import com.gogoring.dongoorami.member.dto.request.MemberLogoutAndQuitRequest;
import com.gogoring.dongoorami.member.dto.request.MemberReissueRequest;
import com.gogoring.dongoorami.member.dto.request.MemberUpdateRequest;
import com.gogoring.dongoorami.member.dto.response.MemberInfoResponse;
import com.gogoring.dongoorami.member.dto.response.MemberUpdateProfileImageResponse;
import com.gogoring.dongoorami.member.dto.response.TokenDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PatchMapping("/members/reissue")
    public ResponseEntity<TokenDto> reissueToken(
            @Valid @RequestBody MemberReissueRequest memberReissueRequest) {
        return ResponseEntity.ok(memberService.reissueToken(memberReissueRequest));
    }

    @PatchMapping("/members/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody MemberLogoutAndQuitRequest memberLogoutAndQuitRequest) {
        memberService.logout(memberLogoutAndQuitRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/members")
    public ResponseEntity<Void> quit(
            @Valid @RequestBody MemberLogoutAndQuitRequest memberLogoutAndQuitRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        memberService.quit(memberLogoutAndQuitRequest, customUserDetails.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/members/profile-image")
    public ResponseEntity<MemberUpdateProfileImageResponse> updateProfileImage(
            @RequestPart("image") MultipartFile multipartFile,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(memberService.updateProfileImage(multipartFile,
                customUserDetails.getId()));
    }

    @PatchMapping("/members")
    public ResponseEntity<MemberInfoResponse> updateMember(
            @Valid @RequestBody MemberUpdateRequest memberUpdateRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(memberService.updateMember(memberUpdateRequest,
                customUserDetails.getId()));
    }
}
