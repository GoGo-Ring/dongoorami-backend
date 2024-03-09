package com.gogoring.dongoorami.accompany.dto.response;

import com.gogoring.dongoorami.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MemberProfile {

    private Long id;

    private String name;

    private String profileImage;

    private String gender;

    private Integer age;

    private String introduction;

    private boolean currentMember;

    public static MemberProfile of(Member member, Long currentMemberId) {
        return MemberProfile.builder()
                .id(member.getId())
                .age(member.getAge())
                .introduction(member.getIntroduction())
                .name(member.getName())
                .profileImage(member.getProfileImage())
                .currentMember(member.getId() == currentMemberId)
                .gender(member.getGender()).build();
    }
}
