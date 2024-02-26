package com.gogoring.dongoorami.accompany.application;

import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsResponse;

public interface AccompanyService {

    Long createAccompanyPost(AccompanyPostRequest accompanyPostRequest, Long memberId);

    AccompanyPostsResponse getAccompanyPosts(Long cursorId, int size);

}
