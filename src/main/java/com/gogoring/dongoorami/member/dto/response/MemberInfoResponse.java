package com.gogoring.dongoorami.member.dto.response;

import com.gogoring.dongoorami.member.domain.Member;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberInfoResponse {

    private final String name;

    private final String profileImage;

    private final String gender;

    private final Integer age;

    private final String introduction;

    @Builder
    public MemberInfoResponse(String name, String profileImage, String gender, Integer age,
            String introduction) {
        this.name = name;
        this.profileImage = profileImage;
        this.gender = gender;
        this.age = age;
        this.introduction = introduction;
    }

    public static MemberInfoResponse of(Member member) {
        return MemberInfoResponse.builder()
                .name(member.getName())
                .profileImage(member.getProfileImage())
                .gender(member.getGender())
                .age((LocalDate.now().getYear() - member.getBirthDate().getYear()) + 1)
                .introduction(member.getIntroduction())
                .build();
    }
}
