package com.gogoring.dongoorami.wish;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.wish.domain.Wish;
import java.util.ArrayList;
import java.util.List;

public class WishDataFactory {

    public static Wish createWish(Member member, AccompanyPost accompanyPost) {
        return Wish.builder()
                .member(member)
                .accompanyPost(accompanyPost)
                .build();
    }

    public static List<Wish> createWishes(Member member, AccompanyPost accompanyPost, int size) {
        List<Wish> wishes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            wishes.add(createWish(member, accompanyPost));
        }

        return wishes;
    }
}
