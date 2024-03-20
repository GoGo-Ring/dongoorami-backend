package com.gogoring.dongoorami.member;

import com.gogoring.dongoorami.global.jwt.CustomUserDetails;
import com.gogoring.dongoorami.member.domain.Member;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

public class MemberDataFactory {

    public static Member createMember() {
        Member member = Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId(UUID.randomUUID().toString())
                .build();
        member.updateNicknameAndGenderAndBirthDate("백둥이", "여자", LocalDate.of(2001, 1, 17));
        member.updateNicknameAndIntroduction("백둥이", "안녕하세요~~");
        return member;
    }

    public static Member createLoginMember() {
        return ((CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()).getMember();
    }

    public static Member createLoginMemberWithNickname() {
        Member member = ((CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()).getMember();
        ReflectionTestUtils.setField(member, "nickname", "김뫄뫄");

        return member;
    }
}
