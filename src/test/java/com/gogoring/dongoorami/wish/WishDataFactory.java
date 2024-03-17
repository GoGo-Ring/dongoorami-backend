package com.gogoring.dongoorami.wish;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.wish.domain.Wish;

public class WishDataFactory {

    public static Wish createWish(Member member, AccompanyPost accompanyPost) {
        return Wish.builder()
                .member(member)
                .accompanyPost(accompanyPost)
                .build();
    }
}
