package com.gogoring.dongoorami.accompany.application;

import com.gogoring.dongoorami.accompany.dto.request.AccompanyCommentRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyCommentsResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsResponse;

public interface AccompanyService {

    Long createAccompanyPost(AccompanyPostRequest accompanyPostRequest, Long memberId);

    AccompanyPostsResponse getAccompanyPosts(Long cursorId, int size);

    AccompanyPostResponse getAccompanyPost(Long accompanyPostId);

    Long createAccompanyComment(Long accompanyPostId,
            AccompanyCommentRequest accompanyCommentRequest, Long memberId);

    AccompanyCommentsResponse getAccompanyComments(Long accompanyPostId);

    void updateAccompanyPost(AccompanyPostRequest accompanyPostRequest, Long memberId,
            Long accompanyPostId);
}