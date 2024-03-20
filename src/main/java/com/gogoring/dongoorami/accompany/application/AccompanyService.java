package com.gogoring.dongoorami.accompany.application;

import com.gogoring.dongoorami.accompany.dto.request.AccompanyCommentRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostFilterRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyReviewRequest;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyCommentsResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyCommentsShortResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsShortResponse;
import com.gogoring.dongoorami.accompany.dto.response.MemberProfile;
import com.gogoring.dongoorami.accompany.dto.response.ReviewsResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface AccompanyService {

    Long createAccompanyPost(AccompanyPostRequest accompanyPostRequest, List<MultipartFile> images,
            Long currentMemberId);

    AccompanyPostsResponse getAccompanyPosts(Long cursorId, int size,
            AccompanyPostFilterRequest accompanyPostFilterRequest);

    AccompanyPostResponse getAccompanyPost(Long currentMemberId, Long accompanyPostId);

    Long createAccompanyComment(Long accompanyPostId,
            AccompanyCommentRequest accompanyCommentRequest, Long currentMemberId,
            Boolean isAccompanyApplyComment);

    AccompanyCommentsResponse getAccompanyComments(Long accompanyPostId, Long currentMemberId);

    void updateAccompanyPost(AccompanyPostRequest accompanyPostRequest, List<MultipartFile> images,
            Long currentMemberId,
            Long accompanyPostId);

    void deleteAccompanyPost(Long currentMemberId, Long accompanyPostId);

    MemberProfile getMemberProfile(Long memberId, Long currentMemberId);

    void updateAccompanyComment(Long accompanyCommentId,
            AccompanyCommentRequest accompanyCommentRequest, Long currentMemberId);

    void deleteAccompanyComment(Long accompanyCommentId, Long currentMemberId);

    Long createAccompanyApplyComment(Long accompanyPostId, Long currentMemberId);

    void confirmAccompany(Long accompanyCommentId, Long currentMemberId);

    List<MemberProfile> getReviewees(Long accompanyPostId, Long currentMemberId);

    void updateAccompanyReview(List<AccompanyReviewRequest> accompanyReviewRequests,
            Long accompanyPostId, Long currentMemberId);

    void updateAccompanyPostStatusCompleted(Long accompanyPostId, Long currentMemberId);

    ReviewsResponse getReceivedReviews(Long cursorId, int size, Long memberId);

    ReviewsResponse getWaitingReviews(Long cursorId, int size, Long currentMemberId);

    AccompanyPostsShortResponse getAccompanyPostsByMember(Long cursorId, int size,
            Long currentMemberId);

    AccompanyCommentsShortResponse getAccompanyCommentsByMember(Long cursorId, int size,
            Long currentMemberId);
}