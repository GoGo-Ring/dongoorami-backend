package com.gogoring.dongoorami.wish.application;

public interface WishService {

    void createWish(Long accompanyPostId, Long memberId);

    void deleteWish(Long accompanyPostId, Long memberId);
}
