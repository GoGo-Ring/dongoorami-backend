package com.gogoring.dongoorami.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberUpdateProfileImageResponse {

    private final String profileImageUrl;

    @Builder
    public MemberUpdateProfileImageResponse(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public static MemberUpdateProfileImageResponse of(String profileImageUrl) {
        return MemberUpdateProfileImageResponse.builder()
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
