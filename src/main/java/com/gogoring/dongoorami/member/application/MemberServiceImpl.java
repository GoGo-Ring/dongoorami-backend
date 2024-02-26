package com.gogoring.dongoorami.member.application;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.gogoring.dongoorami.global.exception.FailFileUploadException;
import com.gogoring.dongoorami.global.exception.GlobalErrorCode;
import com.gogoring.dongoorami.global.jwt.TokenProvider;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.dto.request.MemberLogoutAndQuitRequest;
import com.gogoring.dongoorami.member.dto.request.MemberReissueRequest;
import com.gogoring.dongoorami.member.dto.request.MemberUpdateRequest;
import com.gogoring.dongoorami.member.dto.response.MemberInfoResponse;
import com.gogoring.dongoorami.member.dto.response.MemberUpdateProfileImageResponse;
import com.gogoring.dongoorami.member.dto.response.TokenDto;
import com.gogoring.dongoorami.global.exception.InvalidFileExtensionException;
import com.gogoring.dongoorami.member.exception.InvalidRefreshTokenException;
import com.gogoring.dongoorami.member.exception.MemberErrorCode;
import com.gogoring.dongoorami.member.exception.MemberNotFoundException;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import com.gogoring.dongoorami.member.repository.TokenRepository;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private static final String LOGOUT_VALUE = "logout";

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final AmazonS3 amazonS3;

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
    public MemberUpdateProfileImageResponse updateProfileImage(MultipartFile multipartFile, Long memberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        String originalFilename = multipartFile.getOriginalFilename();
        validateFileExtension(originalFilename);
        String s3Filename = UUID.randomUUID() + "-" + originalFilename;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        try {
            amazonS3.putObject(bucket, s3Filename, multipartFile.getInputStream(), objectMetadata);
        } catch (AmazonS3Exception e) {
            log.error("Amazon S3 error while uploading file: " + e.getMessage());
            throw new FailFileUploadException(GlobalErrorCode.FAIL_FILE_UPLOAD);
        } catch (SdkClientException e) {
            log.error("AWS SDK client error while uploading file: " + e.getMessage());
            throw new FailFileUploadException(GlobalErrorCode.FAIL_FILE_UPLOAD);
        } catch (IOException e) {
            log.error("IO error while uploading file: " + e.getMessage());
            throw new FailFileUploadException(GlobalErrorCode.FAIL_FILE_UPLOAD);
        }

        if (member.getProfileImage() != null) {
            String profileImageUrl = member.getProfileImage();
            String filename = profileImageUrl.substring(profileImageUrl.lastIndexOf(".com/") + 1);
            amazonS3.deleteObject(bucket, filename);
        }
        String newProfileImageUrl = amazonS3.getUrl(bucket, s3Filename).toString();
        member.updateProfileImage(newProfileImageUrl);

        return MemberUpdateProfileImageResponse.of(newProfileImageUrl);
    }

    @Transactional
    @Override
    public MemberInfoResponse updateMember(MemberUpdateRequest memberUpdateRequest, Long memberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        member.updateInfo(memberUpdateRequest.getGender(), memberUpdateRequest.getBirthDate(),
                memberUpdateRequest.getIntroduction());

        return MemberInfoResponse.of(member);
    }

    @Override
    public MemberInfoResponse getMember(Long memberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        return MemberInfoResponse.of(member);
    }

    private void validateFileExtension(String originalFilename) {
        List<String> allowedExtensions = Arrays.asList("jpg", "png", "jpeg");

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1)
                .toLowerCase();
        if (!allowedExtensions.contains(fileExtension)) {
            throw new InvalidFileExtensionException(GlobalErrorCode.INVALID_FILE_EXTENSION);
        }
    }
}
