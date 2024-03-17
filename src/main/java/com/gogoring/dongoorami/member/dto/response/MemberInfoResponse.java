package com.gogoring.dongoorami.member.dto.response;

import com.gogoring.dongoorami.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberInfoResponse {

    private final String nickname;

    private final String profileImage;

    private final String gender;

    private final Integer age;

    private final String introduction;

    private final Double manner;

    @Builder
    public MemberInfoResponse(String nickname, String profileImage, String gender, Integer age,
            String introduction, Double manner) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.gender = gender;
        this.age = age;
        this.introduction = introduction;
        this.manner = manner;
    }

    public static MemberInfoResponse of(Member member) {
        return MemberInfoResponse.builder()
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .gender(member.getGender())
                .age(member.getAge())
                .introduction(member.getIntroduction())
                .manner(member.getManner())
                .build();
    }
}
