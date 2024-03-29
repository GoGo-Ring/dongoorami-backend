package com.gogoring.dongoorami.global.oauth2;

import com.gogoring.dongoorami.member.domain.Member;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuth2UserInfo {

    private final String providerId;
    private final String provider;
    private final String profileImage;

    @Builder
    public OAuth2UserInfo(String providerId, String provider, String profileImage) {
        this.providerId = providerId;
        this.provider = provider;
        this.profileImage = profileImage;
    }

    public static OAuth2UserInfo of(String provider, Map<String, Object> attributes) {
        if ("kakao".equals(provider)) {
            return ofKakao(attributes);
        } else if ("google".equals(provider)) {
            return ofGoogle(attributes);
        } else if ("naver".equals(provider)) {
            return ofNaver((Map<String, Object>) attributes.get("response"));
        }

        throw new IllegalArgumentException("소셜 로그인 방식이 존재하지 않습니다.");
    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuth2UserInfo.builder()
                .providerId(attributes.get("id").toString())
                .provider("kakao")
                .profileImage((String) kakaoProfile.get("profile_image_url"))
                .build();
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .providerId((String) attributes.get("sub"))
                .provider("google")
                .profileImage((String) attributes.get("picture"))
                .build();
    }

    private static OAuth2UserInfo ofNaver(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .providerId((String) attributes.get("id"))
                .provider("naver")
                .profileImage((String) attributes.get("profile_image"))
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .profileImage(getProfileImage())
                .provider(getProvider())
                .providerId(getProviderId())
                .build();
    }
}
