package com.gogoring.dongoorami.member;

import com.gogoring.dongoorami.global.jwt.CustomUserDetails;
import com.gogoring.dongoorami.member.domain.Member;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

public class MemberDataFactory {

    public static Member createMember() {
        return Member.builder()
                .profileImage("image.png")
                .provider("kakao")
                .providerId("alsjkghlaskdjgh")
                .build();
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
