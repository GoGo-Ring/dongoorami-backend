package com.gogoring.dongoorami.accompany.application;

import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyCommentRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostFilterRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyCommentsResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyCommentsResponse.AccompanyCommentInfo;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsResponse.AccompanyPostInfo;
import com.gogoring.dongoorami.accompany.dto.response.MemberProfile;
import com.gogoring.dongoorami.accompany.exception.AccompanyApplyCommentModifyDeniedException;
import com.gogoring.dongoorami.accompany.exception.AccompanyApplyNotAllowedForWriterException;
import com.gogoring.dongoorami.accompany.exception.AccompanyErrorCode;
import com.gogoring.dongoorami.accompany.exception.AccompanyPostNotFoundException;
import com.gogoring.dongoorami.accompany.exception.DuplicatedAccompanyApplyException;
import com.gogoring.dongoorami.accompany.exception.OnlyWriterCanModifyException;
import com.gogoring.dongoorami.accompany.repository.AccompanyCommentRepository;
import com.gogoring.dongoorami.accompany.repository.AccompanyPostRepository;
import com.gogoring.dongoorami.global.common.BaseEntity;
import com.gogoring.dongoorami.global.util.ImageType;
import com.gogoring.dongoorami.global.util.S3ImageUtil;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.exception.MemberErrorCode;
import com.gogoring.dongoorami.member.exception.MemberNotFoundException;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AccompanyServiceImpl implements AccompanyService {

    private final AccompanyPostRepository accompanyPostRepository;
    private final AccompanyCommentRepository accompanyCommentRepository;
    private final MemberRepository memberRepository;
    private final S3ImageUtil s3ImageUtil;

    @Override
    public Long createAccompanyPost(AccompanyPostRequest accompanyPostRequest,
            List<MultipartFile> images, Long currentMemberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(currentMemberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        List<String> imageUrls = s3ImageUtil.putObjects(images,
                ImageType.ACCOMPANY_POST);

        return accompanyPostRepository.save(accompanyPostRequest.toEntity(member, imageUrls))
                .getId();
    }

    @Override
    public AccompanyPostsResponse getAccompanyPosts(Long cursorId, int size,
            AccompanyPostFilterRequest accompanyPostFilterRequest) {
        Slice<AccompanyPost> accompanyPosts = accompanyPostRepository.findByAccompanyPostFilterRequest(
                cursorId, size, accompanyPostFilterRequest);

        return new AccompanyPostsResponse(
                accompanyPosts.hasNext(),
                accompanyPosts.getContent().stream().map(
                        AccompanyPostInfo::of).toList());
    }

    @Transactional
    @Override
    public AccompanyPostResponse getAccompanyPost(Long currentMemberId, Long accompanyPostId) {
        AccompanyPost accompanyPost = accompanyPostRepository.findByIdAndIsActivatedIsTrue(
                        accompanyPostId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND));
        accompanyPost.increaseViewCount();
        Long waitingCount = accompanyCommentRepository.countByAccompanyPostIdAndIsActivatedIsTrueAndIsAccompanyApplyCommentTrue(
                accompanyPostId);

        return AccompanyPostResponse.of(accompanyPost, waitingCount,
                MemberProfile.of(accompanyPost.getMember(), currentMemberId));
    }

    @Override
    public Long createAccompanyComment(Long accompanyPostId,
            AccompanyCommentRequest accompanyCommentRequest, Long currentMemberId,
            Boolean isAccompanyApplyComment) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(currentMemberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        AccompanyPost accompanyPost = accompanyPostRepository.findByIdAndIsActivatedIsTrue(
                        accompanyPostId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND));
        AccompanyComment accompanyComment = accompanyCommentRequest.toEntity(member,
                isAccompanyApplyComment);
        accompanyPost.addAccompanyComment(accompanyComment);
        accompanyCommentRepository.save(accompanyComment);

        return accompanyPost.getId();
    }

    @Override
    public AccompanyCommentsResponse getAccompanyComments(Long accompanyPostId,
            Long currentMemberId) {
        AccompanyPost accompanyPost = accompanyPostRepository.findByIdAndIsActivatedIsTrue(
                        accompanyPostId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND));
        List<AccompanyCommentInfo> accompanyCommentInfos = accompanyPost.getAccompanyComments()
                .stream()
                .filter(AccompanyComment::isActivated)
                .map((AccompanyComment accompanyComment) -> AccompanyCommentInfo.of(
                        accompanyComment, currentMemberId))
                .toList();

        return new AccompanyCommentsResponse(accompanyCommentInfos);
    }

    @Transactional
    @Override
    public void updateAccompanyPost(AccompanyPostRequest accompanyPostRequest,
            List<MultipartFile> images, Long currentMemberId,
            Long accompanyPostId) {
        AccompanyPost accompanyPost = accompanyPostRepository.findByIdAndIsActivatedIsTrue(
                        accompanyPostId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND));
        checkMemberIsWriter(accompanyPost.getMember().getId(), currentMemberId);
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(currentMemberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        List<String> imageUrls = s3ImageUtil.putObjects(images,
                ImageType.ACCOMPANY_POST);
        s3ImageUtil.deleteObjects(accompanyPost.getImages(), ImageType.ACCOMPANY_POST);
        accompanyPost.update(accompanyPostRequest.toEntity(member, imageUrls));
    }

    @Transactional
    @Override
    public void deleteAccompanyPost(Long currentMemberId, Long accompanyPostId) {
        AccompanyPost accompanyPost = accompanyPostRepository.findByIdAndIsActivatedIsTrue(
                        accompanyPostId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND));
        checkMemberIsWriter(accompanyPost.getMember().getId(), currentMemberId);
        accompanyPost.getAccompanyComments().forEach(BaseEntity::updateIsActivatedFalse);
        accompanyPost.updateIsActivatedFalse();
    }

    @Override
    public MemberProfile getMemberProfile(Long memberId, Long currentMemberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        return MemberProfile.of(member, currentMemberId);
    }

    @Transactional
    @Override
    public void updateAccompanyComment(Long accompanyCommentId,
            AccompanyCommentRequest accompanyCommentRequest, Long currentMemberId) {
        AccompanyComment accompanyComment = accompanyCommentRepository.findByIdAndIsActivatedIsTrue(
                        accompanyCommentId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_COMMENT_NOT_FOUND));
        checkMemberIsWriter(accompanyComment.getMember().getId(), currentMemberId);
        checkIsAccompanyApplyComment(accompanyComment.getIsAccompanyApplyComment());
        accompanyComment.updateContent(accompanyCommentRequest.getContent());
    }

    @Transactional
    @Override
    public void deleteAccompanyComment(Long accompanyCommentId, Long currentMemberId) {
        AccompanyComment accompanyComment = accompanyCommentRepository.findByIdAndIsActivatedIsTrue(
                        accompanyCommentId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_COMMENT_NOT_FOUND));
        checkMemberIsWriter(accompanyComment.getMember().getId(), currentMemberId);
        accompanyComment.updateIsActivatedFalse();
    }

    @Override
    public Long createAccompanyApplyComment(Long accompanyPostId, Long currentMemberId) {
        AccompanyPost accompanyPost = accompanyPostRepository.findByIdAndIsActivatedIsTrue(
                        accompanyPostId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND));
        checkApplicantIsWriter(currentMemberId, accompanyPost.getMember().getId());
        checkDuplicatedAccompanyApply(accompanyPostId, currentMemberId);
        return createAccompanyComment(accompanyPostId,
                AccompanyCommentRequest.createAccompanyApplyCommentRequest(),
                currentMemberId, true);
    }

    private void checkMemberIsWriter(Long memberId, Long writerId) {
        if (!writerId.equals(memberId)) {
            throw new OnlyWriterCanModifyException(AccompanyErrorCode.ONLY_WRITER_CAN_MODIFY);
        }
    }

    private void checkDuplicatedAccompanyApply(Long accompanyPostId, Long memberId) {
        if (accompanyCommentRepository.existsByAccompanyPostIdAndMemberIdAndIsAccompanyApplyCommentTrue(
                accompanyPostId, memberId)) {
            throw new DuplicatedAccompanyApplyException(
                    AccompanyErrorCode.DUPLICATED_ACCOMPANY_APPLY);
        }
    }

    private void checkIsAccompanyApplyComment(Boolean isAccompanyApplyComment) {
        if (Boolean.TRUE.equals(isAccompanyApplyComment)) {
            throw new AccompanyApplyCommentModifyDeniedException(
                    AccompanyErrorCode.ACCOMPANY_APPLY_COMMENT_MODIFY_DENIED);
        }
    }

    private void checkApplicantIsWriter(Long applicantId, Long writerId) {
        if (applicantId.equals(writerId)) {
            throw new AccompanyApplyNotAllowedForWriterException(
                    AccompanyErrorCode.ACCOMPANY_APPLY_NOT_ALLOWED_FOR_WRITER);
        }
    }

}
