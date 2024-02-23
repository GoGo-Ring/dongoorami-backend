package com.gogoring.dongoorami.member.application;

import com.gogoring.dongoorami.member.dto.request.MemberLogoutAndQuitRequest;
import com.gogoring.dongoorami.member.dto.request.MemberReissueRequest;
import com.gogoring.dongoorami.member.dto.response.TokenDto;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    TokenDto reissueToken(MemberReissueRequest memberReissueRequest);

    void logout(MemberLogoutAndQuitRequest memberLogoutAndQuitRequest);

    void quit(MemberLogoutAndQuitRequest memberLogoutAndQuitRequest, Long memberId);

    String updateProfileImage(MultipartFile multipartFile, Long memberId);
}
