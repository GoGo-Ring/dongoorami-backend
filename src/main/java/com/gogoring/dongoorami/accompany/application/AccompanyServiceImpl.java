package com.gogoring.dongoorami.accompany.application;

import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyCommentRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyCommentsResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyCommentsResponse.AccompanyCommentInfo;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsResponse.AccompanyPostInfo;
import com.gogoring.dongoorami.accompany.dto.response.MemberInfo;
import com.gogoring.dongoorami.accompany.exception.AccompanyErrorCode;
import com.gogoring.dongoorami.accompany.exception.AccompanyPostNotFoundException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccompanyServiceImpl implements AccompanyService {

    private final AccompanyPostRepository accompanyPostRepository;
    private final AccompanyCommentRepository accompanyCommentRepository;
    private final MemberRepository memberRepository;
    private final S3ImageUtil s3ImageUtil;

    @Override
    public Long createAccompanyPost(AccompanyPostRequest accompanyPostRequest, Long memberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        List<String> imageUrls = s3ImageUtil.putObjects(accompanyPostRequest.getImages(),
                ImageType.ACCOMPANY_POST);

        return accompanyPostRepository.save(accompanyPostRequest.toEntity(member, imageUrls))
                .getId();
    }

    @Override
    public AccompanyPostsResponse getAccompanyPosts(Long cursorId, int size) {
        Slice<AccompanyPost> accompanyPosts;
        if (cursorId == null) {
            accompanyPosts = accompanyPostRepository.findAllByOrderByIdDesc(
                    PageRequest.of(0, size));
        } else {
            accompanyPosts = accompanyPostRepository.findByIdLessThanOrderByIdDesc(
                    cursorId, PageRequest.of(0, size));
        }

        return new AccompanyPostsResponse(
                accompanyPosts.hasNext(),
                accompanyPosts.getContent().stream()
                        .map(AccompanyPostInfo::of)
                        .toList());
    }

    @Transactional
    @Override
    public AccompanyPostResponse getAccompanyPost(Long accompanyPostId) {
        AccompanyPost accompanyPost = accompanyPostRepository.findByIdAndIsActivatedIsTrue(
                        accompanyPostId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND));
        accompanyPost.increaseViewCount();

        return AccompanyPostResponse.of(accompanyPost,
                MemberInfo.of(accompanyPost.getMember()));
    }

    @Override
    public Long createAccompanyComment(Long accompanyPostId,
            AccompanyCommentRequest accompanyCommentRequest, Long memberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        AccompanyPost accompanyPost = accompanyPostRepository.findByIdAndIsActivatedIsTrue(
                        accompanyPostId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND));
        AccompanyComment accompanyComment = accompanyCommentRequest.toEntity(member);
        accompanyPost.addAccompanyComment(accompanyComment);
        accompanyCommentRepository.save(accompanyComment);

        return accompanyPost.getId();
    }

    @Override
    public AccompanyCommentsResponse getAccompanyComments(Long accompanyPostId) {
        AccompanyPost accompanyPost = accompanyPostRepository.findByIdAndIsActivatedIsTrue(
                        accompanyPostId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND));
        List<AccompanyCommentInfo> accompanyCommentInfos = accompanyPost.getAccompanyComments()
                .stream()
                .filter(AccompanyComment::isActivated)
                .map(AccompanyCommentInfo::of)
                .toList();

        return new AccompanyCommentsResponse(accompanyCommentInfos);
    }

    @Transactional
    @Override
    public void updateAccompanyPost(AccompanyPostRequest accompanyPostRequest, Long memberId,
            Long accompanyPostId) {
        AccompanyPost accompanyPost = accompanyPostRepository.findByIdAndIsActivatedIsTrue(
                        accompanyPostId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND));
        checkMemberIsWriter(accompanyPost, memberId);
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        List<String> imageUrls = s3ImageUtil.putObjects(accompanyPostRequest.getImages(),
                ImageType.ACCOMPANY_POST);
        s3ImageUtil.deleteObjects(accompanyPost.getImages(), ImageType.ACCOMPANY_POST);
        accompanyPost.update(accompanyPostRequest.toEntity(member, imageUrls));
    }

    @Transactional
    @Override
    public void deleteAccompanyPost(Long memberId, Long accompanyPostId) {
        AccompanyPost accompanyPost = accompanyPostRepository.findByIdAndIsActivatedIsTrue(
                        accompanyPostId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND));
        checkMemberIsWriter(accompanyPost, memberId);
        accompanyPost.getAccompanyComments().forEach(BaseEntity::updateIsActivatedFalse);
        accompanyPost.updateIsActivatedFalse();
    }

    private void checkMemberIsWriter(AccompanyPost accompanyPost, Long memberId) {
        if (accompanyPost.getMember().getId() != memberId) {
            throw new OnlyWriterCanModifyException(AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND);
        }
    }

}
