package com.gogoring.dongoorami.member.application;

import com.gogoring.dongoorami.global.jwt.TokenProvider;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.dto.request.MemberReissueRequest;
import com.gogoring.dongoorami.member.dto.response.TokenDto;
import com.gogoring.dongoorami.member.exception.InvalidRefreshTokenException;
import com.gogoring.dongoorami.member.exception.MemberErrorCode;
import com.gogoring.dongoorami.member.exception.MemberNotFoundException;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public TokenDto reissueToken(MemberReissueRequest memberReissueRequest) {
        if (!tokenProvider.validateRefreshToken(memberReissueRequest.getRefreshToken())) {
            throw new InvalidRefreshTokenException(MemberErrorCode.INVALID_REFRESH_TOKEN);
        }

        String providerId = tokenProvider.getProviderId(memberReissueRequest.getRefreshToken());
        Member member = memberRepository.findByProviderIdAndIsActivatedIsTrue(providerId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        String accessToken = tokenProvider.createAccessToken(member.getProviderId(),
                member.getRoles());
        String refreshToken = tokenProvider.createRefreshToken(member.getProviderId());

        return TokenDto.of(accessToken, refreshToken);
    }
}
