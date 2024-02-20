package com.gogoring.dongoorami.member.application;

import com.gogoring.dongoorami.global.jwt.TokenProvider;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.dto.request.MemberLogoutAndQuitRequest;
import com.gogoring.dongoorami.member.dto.request.MemberReissueRequest;
import com.gogoring.dongoorami.member.dto.response.TokenDto;
import com.gogoring.dongoorami.member.exception.InvalidRefreshTokenException;
import com.gogoring.dongoorami.member.exception.MemberErrorCode;
import com.gogoring.dongoorami.member.exception.MemberNotFoundException;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import com.gogoring.dongoorami.member.repository.TokenRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private static final String LOGOUT_VALUE = "logout";

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;

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

    @Override
    public void logout(MemberLogoutAndQuitRequest memberLogoutAndQuitRequest) {
        String providerId = tokenProvider.getProviderId(memberLogoutAndQuitRequest.getRefreshToken());
        tokenRepository.deleteByKey(providerId);

        String accessToken = tokenProvider.getTokenWithNoPrefix(memberLogoutAndQuitRequest.getAccessToken());
        Duration expirationTime = tokenProvider.getRestExpirationTime(accessToken);
        tokenRepository.save(accessToken, LOGOUT_VALUE, expirationTime);
    }

    @Transactional
    @Override
    public void quit(MemberLogoutAndQuitRequest memberLogoutAndQuitRequest, Long memberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        member.updateIsActivatedFalse();

        logout(memberLogoutAndQuitRequest);
    }
}
