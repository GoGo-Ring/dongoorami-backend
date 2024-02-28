package com.gogoring.dongoorami.member.application;

import com.gogoring.dongoorami.member.dto.request.MemberLogoutAndQuitRequest;
import com.gogoring.dongoorami.member.dto.request.MemberReissueRequest;
import com.gogoring.dongoorami.member.dto.request.MemberUpdateRequest;
import com.gogoring.dongoorami.member.dto.response.MemberInfoResponse;
import com.gogoring.dongoorami.member.dto.response.MemberUpdateProfileImageResponse;
import com.gogoring.dongoorami.member.dto.response.TokenDto;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    TokenDto reissueToken(MemberReissueRequest memberReissueRequest);

    void logout(MemberLogoutAndQuitRequest memberLogoutAndQuitRequest);

    void quit(MemberLogoutAndQuitRequest memberLogoutAndQuitRequest, Long memberId);

    MemberUpdateProfileImageResponse updateProfileImage(MultipartFile multipartFile, Long memberId);

    MemberInfoResponse updateMember(MemberUpdateRequest memberUpdateRequest, Long memberId);

    MemberInfoResponse getMember(Long memberId);
}
