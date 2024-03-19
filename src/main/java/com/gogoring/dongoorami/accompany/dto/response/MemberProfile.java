package com.gogoring.dongoorami.accompany.dto.response;

import com.gogoring.dongoorami.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MemberProfile {

    private final Long id;

    private final String nickname;

    private final String profileImage;

    private final String gender;

    private final Integer age;

    private final String introduction;

    private final boolean currentMember;

    private final Integer manner;

    public static MemberProfile of(Member member, Long currentMemberId) {
        return MemberProfile.builder()
                .id(member.getId())
                .age(member.getAge())
                .introduction(member.getIntroduction())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .currentMember(member.getId().equals(currentMemberId))
                .gender(member.getGender())
                .manner(member.getManner())
                .build();
    }
}
