package com.gogoring.dongoorami.accompany.dto.response;

import com.gogoring.dongoorami.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MemberInfo {

    private Long id;

    private String name;

    private String profileImage;

    private String gender;

    private Integer age;

    private String introduction;

    public static MemberInfo of(Member member) {
        return MemberInfo.builder()
                .id(member.getId())
                .age(member.getAge())
                .introduction(member.getIntroduction())
                .name(member.getName())
                .profileImage(member.getProfileImage())
                .gender(member.getGender()).build();
    }
}
