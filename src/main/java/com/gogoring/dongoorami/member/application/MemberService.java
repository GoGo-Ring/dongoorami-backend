package com.gogoring.dongoorami.member.application;

import com.gogoring.dongoorami.member.dto.request.MemberLogoutRequest;
import com.gogoring.dongoorami.member.dto.request.MemberReissueRequest;
import com.gogoring.dongoorami.member.dto.response.TokenDto;

public interface MemberService {

    TokenDto reissueToken(MemberReissueRequest memberReissueRequest);

    void logout(MemberLogoutRequest memberLogoutRequest);
}
