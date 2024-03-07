package com.gogoring.dongoorami.accompany.application;

import com.gogoring.dongoorami.accompany.dto.request.AccompanyCommentRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostFilterRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyCommentsResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface AccompanyService {

    Long createAccompanyPost(AccompanyPostRequest accompanyPostRequest, List<MultipartFile> images,
            Long memberId);

    AccompanyPostsResponse getAccompanyPosts(Long cursorId, int size,
            AccompanyPostFilterRequest accompanyPostFilterRequest);

    AccompanyPostResponse getAccompanyPost(Long memberId, Long accompanyPostId);

    Long createAccompanyComment(Long accompanyPostId,
            AccompanyCommentRequest accompanyCommentRequest, Long memberId);

    AccompanyCommentsResponse getAccompanyComments(Long accompanyPostId);

    void updateAccompanyPost(AccompanyPostRequest accompanyPostRequest, List<MultipartFile> images,
            Long memberId,
            Long accompanyPostId);

    void deleteAccompanyPost(Long memberId, Long accompanyPostId);
}