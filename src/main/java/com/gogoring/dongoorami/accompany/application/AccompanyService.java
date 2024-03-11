package com.gogoring.dongoorami.accompany.application;

import com.gogoring.dongoorami.accompany.dto.request.AccompanyCommentRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostFilterRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyCommentsResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsResponse;
import com.gogoring.dongoorami.accompany.dto.response.MemberProfile;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface AccompanyService {

    Long createAccompanyPost(AccompanyPostRequest accompanyPostRequest, List<MultipartFile> images,
            Long currentMemberId);

    AccompanyPostsResponse getAccompanyPosts(Long cursorId, int size,
            AccompanyPostFilterRequest accompanyPostFilterRequest);

    AccompanyPostResponse getAccompanyPost(Long currentMemberId, Long accompanyPostId);

    Long createAccompanyComment(Long accompanyPostId,
            AccompanyCommentRequest accompanyCommentRequest, Long currentMemberId);

    AccompanyCommentsResponse getAccompanyComments(Long accompanyPostId, Long currentMemberId);

    void updateAccompanyPost(AccompanyPostRequest accompanyPostRequest, List<MultipartFile> images,
            Long currentMemberId,
            Long accompanyPostId);

    void deleteAccompanyPost(Long currentMemberId, Long accompanyPostId);

    MemberProfile getMemberProfile(Long memberId, Long currentMemberId);

    void updateAccompanyComment(Long accompanyCommentId,
            AccompanyCommentRequest accompanyCommentRequest, Long currentMemberId);
}