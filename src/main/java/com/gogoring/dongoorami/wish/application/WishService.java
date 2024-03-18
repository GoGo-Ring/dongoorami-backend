package com.gogoring.dongoorami.wish.application;

import com.gogoring.dongoorami.wish.dto.response.WishesGetResponse;

public interface WishService {

    void createWish(Long accompanyPostId, Long memberId);

    void deleteWish(Long accompanyPostId, Long memberId);

    WishesGetResponse getWishes(Long cursorId, int size, Long memberId);
}
