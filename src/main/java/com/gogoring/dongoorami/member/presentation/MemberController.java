package com.gogoring.dongoorami.member.presentation;

import com.gogoring.dongoorami.member.application.MemberService;
import com.gogoring.dongoorami.member.dto.request.MemberReissueRequest;
import com.gogoring.dongoorami.member.dto.response.TokenDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
