package com.gogoring.dongoorami.global.oauth2;

import com.gogoring.dongoorami.member.domain.Member;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getName() {
        return (String)((Map)((Map)attributes.get("kakao_account")).get("profile")).get("nickname");
    }

    @Override
    public String getProfileImage() {
        return (String)((Map)((Map)attributes.get("kakao_account")).get("profile")).get("profile_image_url");
    }

    @Override
    public Member toEntity() {
        return Member.builder()
                .name(getName())
                .profileImage(getProfileImage())
                .provider(getProvider())
                .providerId(getProviderId())
                .build();
    }
}
