package com.gogoring.dongoorami.global.util;

import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.domain.ConcertReview;
import com.gogoring.dongoorami.global.jwt.CustomUserDetails;
import com.gogoring.dongoorami.member.domain.Member;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;

public class TestDataUtil {

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

    public static Concert createConcert() {
        return Concert.builder()
                .kopisId("abcefg")
                .name("고고링 백걸즈의 스프링 탐방기")
                .build();
    }

    public static List<ConcertReview> createConcertReviews(Concert concert, Member member, int size) {
        List<ConcertReview> concertReviews = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            concertReviews.add(ConcertReview.builder()
                    .concert(concert)
                    .member(member)
                    .title("최고의 공연입니다~")
                    .content("재관람 의향 있어요 너무너무 재밌었습니다!")
                    .rating(5)
                    .build());
        }

        return concertReviews;
    }
}
