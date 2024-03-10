package com.gogoring.dongoorami.global.util;

import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.global.jwt.CustomUserDetails;
import com.gogoring.dongoorami.member.domain.Member;
import org.springframework.security.core.context.SecurityContextHolder;

public class TestDataUtil {

    public static Member createMember() {
        return Member.builder()
                .name("김뫄뫄")
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

    public static Concert createConcert() {
        return Concert.builder()
                .kopisId("abcefg")
                .name("고고링 백걸즈의 스프링 탐방기")
                .build();
    }
}
