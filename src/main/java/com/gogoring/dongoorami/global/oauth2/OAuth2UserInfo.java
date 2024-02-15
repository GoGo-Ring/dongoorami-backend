package com.gogoring.dongoorami.global.oauth2;

import com.gogoring.dongoorami.member.domain.Member;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getName();
    String getProfileImage();
    Member toEntity();
}
