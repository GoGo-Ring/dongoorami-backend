package com.gogoring.dongoorami.member.application;

import com.gogoring.dongoorami.global.jwt.TokenProvider;
import com.gogoring.dongoorami.global.util.ImageType;
import com.gogoring.dongoorami.global.util.S3ImageUtil;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.dto.request.MemberLogoutAndQuitRequest;
import com.gogoring.dongoorami.member.dto.request.MemberReissueRequest;
import com.gogoring.dongoorami.member.dto.response.MemberUpdateProfileImageResponse;
import com.gogoring.dongoorami.member.dto.response.TokenDto;
import com.gogoring.dongoorami.member.exception.InvalidRefreshTokenException;
import com.gogoring.dongoorami.member.exception.MemberErrorCode;
import com.gogoring.dongoorami.member.exception.MemberNotFoundException;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import com.gogoring.dongoorami.member.repository.TokenRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private static final String LOGOUT_VALUE = "logout";

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final S3ImageUtil s3ImageUtil;

    @Value("${cloud.aws.s3.bucket}/member")
    private String bucket;

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
        String providerId = tokenProvider.getProviderId(
                memberLogoutAndQuitRequest.getRefreshToken());
        tokenRepository.deleteByKey(providerId);

        String accessToken = tokenProvider.getTokenWithNoPrefix(
                memberLogoutAndQuitRequest.getAccessToken());
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

    @Transactional
    @Override
    public MemberUpdateProfileImageResponse updateProfileImage(MultipartFile multipartFile,
            Long memberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        String newProfileImageUrl = s3ImageUtil.putObject(multipartFile, ImageType.MEMBER);
        if (member.getProfileImage() != null) {
            s3ImageUtil.deleteObject(member.getProfileImage(), ImageType.MEMBER);
        }
        member.updateProfileImage(newProfileImageUrl);

        return MemberUpdateProfileImageResponse.of(newProfileImageUrl);
    }
}
