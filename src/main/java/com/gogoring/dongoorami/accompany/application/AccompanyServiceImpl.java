package com.gogoring.dongoorami.accompany.application;

import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.domain.AccompanyReview;
import com.gogoring.dongoorami.accompany.domain.AccompanyReview.AccompanyReviewStatusType;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyCommentRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostFilterRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyReviewRequest;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyCommentShortResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyCommentsResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyCommentsResponse.AccompanyCommentInfo;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyCommentsShortResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostShortResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsResponse;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsResponse.AccompanyPostInfo;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsShortResponse;
import com.gogoring.dongoorami.accompany.dto.response.MemberProfile;
import com.gogoring.dongoorami.accompany.dto.response.ReviewResponse;
import com.gogoring.dongoorami.accompany.dto.response.ReviewsResponse;
import com.gogoring.dongoorami.accompany.exception.AccompanyApplyCommentModifyDeniedException;
import com.gogoring.dongoorami.accompany.exception.AccompanyApplyNotAllowedForWriterException;
import com.gogoring.dongoorami.accompany.exception.AccompanyCommentApplyConfirmDeniedException;
import com.gogoring.dongoorami.accompany.exception.AccompanyErrorCode;
import com.gogoring.dongoorami.accompany.exception.AccompanyPostNotFoundException;
import com.gogoring.dongoorami.accompany.exception.AlreadyApplyConfirmedAccompanyCommentException;
import com.gogoring.dongoorami.accompany.exception.DuplicatedAccompanyApplyException;
import com.gogoring.dongoorami.accompany.exception.OnlyWriterCanConfirmApplyException;
import com.gogoring.dongoorami.accompany.repository.AccompanyCommentRepository;
import com.gogoring.dongoorami.accompany.repository.AccompanyPostRepository;
import com.gogoring.dongoorami.accompany.repository.AccompanyReviewRepository;
import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.exception.ConcertErrorCode;
import com.gogoring.dongoorami.concert.exception.ConcertNotFoundException;
import com.gogoring.dongoorami.concert.repository.ConcertRepository;
import com.gogoring.dongoorami.global.util.ImageType;
import com.gogoring.dongoorami.global.util.S3ImageUtil;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.exception.MemberErrorCode;
import com.gogoring.dongoorami.member.exception.MemberNotFoundException;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import java.util.ArrayList;
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
    private final AccompanyReviewRepository accompanyReviewRepository;
    private final MemberRepository memberRepository;
    private final ConcertRepository concertRepository;
    private final S3ImageUtil s3ImageUtil;

    @Transactional
    @Override
    public Long createAccompanyPost(AccompanyPostRequest accompanyPostRequest,
            List<MultipartFile> images, Long currentMemberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(currentMemberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        List<String> imageUrls =
                !images.get(0).isEmpty() ? s3ImageUtil.putObjects(images, ImageType.ACCOMPANY_POST)
                        : new ArrayList<>();
        Concert concert = concertRepository.findByIdAndIsActivatedIsTrue(
                accompanyPostRequest.getConcertId()).orElseThrow(
                () -> new ConcertNotFoundException(ConcertErrorCode.CONCERT_NOT_FOUND));

        return accompanyPostRepository.save(
                        accompanyPostRequest.toEntity(concert, member, imageUrls))
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
                        accompanyPost -> {
                            long commentCount = accompanyCommentRepository.countByAccompanyPostIdAndIsActivatedIsTrue(
                                    accompanyPost.getId());
                            return AccompanyPostInfo.of(accompanyPost, commentCount);
                        }).toList());
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
                MemberProfile.of(accompanyPost.getWriter(), currentMemberId));
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
        AccompanyComment accompanyComment = accompanyCommentRequest.toEntity(accompanyPost, member,
                isAccompanyApplyComment);
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
        List<AccompanyCommentInfo> accompanyCommentInfos = accompanyCommentRepository.findAllByAccompanyPostIdAndIsActivatedIsTrue(
                        accompanyPost.getId())
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
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(currentMemberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        List<String> imageUrls =
                !images.get(0).isEmpty() ? s3ImageUtil.putObjects(images, ImageType.ACCOMPANY_POST)
                        : new ArrayList<>();
        Concert concert = concertRepository.findByIdAndIsActivatedIsTrue(
                accompanyPostRequest.getConcertId()).orElseThrow(
                () -> new ConcertNotFoundException(ConcertErrorCode.CONCERT_NOT_FOUND));
        accompanyPost.update(accompanyPostRequest.toEntity(concert, member, imageUrls),
                currentMemberId);
        s3ImageUtil.deleteObjects(accompanyPost.getImages(), ImageType.ACCOMPANY_POST);
    }

    @Transactional
    @Override
    public void deleteAccompanyPost(Long currentMemberId, Long accompanyPostId) {
        AccompanyPost accompanyPost = accompanyPostRepository.findByIdAndIsActivatedIsTrue(
                        accompanyPostId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND));
        accompanyCommentRepository.findAllByAccompanyPostIdAndIsActivatedIsTrue(accompanyPostId)
                .forEach(accompanyComment -> accompanyComment.updateIsActivatedFalse(
                        currentMemberId));
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
        checkIsAccompanyApplyComment(accompanyComment.getIsAccompanyApplyComment());
        accompanyComment.updateContent(accompanyCommentRequest.getContent(), currentMemberId);
    }

    @Transactional
    @Override
    public void deleteAccompanyComment(Long accompanyCommentId, Long currentMemberId) {
        AccompanyComment accompanyComment = accompanyCommentRepository.findByIdAndIsActivatedIsTrue(
                        accompanyCommentId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_COMMENT_NOT_FOUND));
        accompanyComment.updateIsActivatedFalse(currentMemberId);
    }

    @Override
    public Long createAccompanyApplyComment(Long accompanyPostId, Long currentMemberId) {
        AccompanyPost accompanyPost = accompanyPostRepository.findByIdAndIsActivatedIsTrue(
                        accompanyPostId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND));
        checkApplicantIsWriter(currentMemberId, accompanyPost.getWriter().getId());
        checkDuplicatedAccompanyApply(accompanyPostId, currentMemberId);
        return createAccompanyComment(accompanyPostId,
                AccompanyCommentRequest.createAccompanyApplyCommentRequest(),
                currentMemberId, true);
    }

    @Transactional
    @Override
    public void confirmAccompany(Long accompanyCommentId, Long currentMemberId) {
        AccompanyComment accompanyComment = accompanyCommentRepository.findByIdAndIsActivatedIsTrue(
                        accompanyCommentId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_COMMENT_NOT_FOUND));
        AccompanyPost accompanyPost = accompanyComment.getAccompanyPost();
        checkConfirmerIsWriter(currentMemberId, accompanyPost.getWriter().getId());
        checkAccompanyCommentIsApplyComment(accompanyComment);
        checkAlreadyConfirmedAccompanyApplyComment(accompanyComment);
        List<Member> accompanyConfirmedMembers = getAccompanyConfirmedMembers(accompanyPost,
                accompanyComment.getMember());
        if (accompanyPost.getTotalPeople() == accompanyConfirmedMembers.size()) {
            accompanyPost.updateStatus();
        }
        createAccompanyReview(accompanyPost, accompanyConfirmedMembers);
        accompanyComment.updateIsAccompanyConfirmedComment();
    }

    @Override
    public List<MemberProfile> getReviewees(Long accompanyPostId, Long currentMemberId) {
        AccompanyPost accompanyPost = accompanyPostRepository.findByIdAndIsActivatedIsTrue(
                        accompanyPostId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND));
        return getCompanions(accompanyPost, currentMemberId).stream()
                .map(companion -> MemberProfile.of(companion, currentMemberId)).toList();
    }

    @Transactional
    @Override
    public void updateAccompanyReview(List<AccompanyReviewRequest> accompanyReviewRequests,
            Long accompanyPostId, Long currentMemberId) {
        accompanyReviewRequests.forEach(accompanyReviewRequest -> {
                    accompanyReviewRepository.findAccompanyReviewByReviewerIdAndRevieweeIdAndAccompanyPostId(
                                    currentMemberId, accompanyReviewRequest.getMemberId(), accompanyPostId)
                            .update(accompanyReviewRequest);
                    Member member = memberRepository.findByIdAndIsActivatedIsTrue(
                                    accompanyReviewRequest.getMemberId())
                            .orElseThrow(
                                    () -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
                    member.updateManner(
                            accompanyReviewRepository.averageRatingPercentByRevieweeId(member.getId()));
                }
        );
    }

    @Transactional
    @Override
    public void updateAccompanyPostStatusCompleted(Long accompanyPostId, Long currentMemberId) {
        AccompanyPost accompanyPost = accompanyPostRepository.findByIdAndIsActivatedIsTrue(
                        accompanyPostId)
                .orElseThrow(() -> new AccompanyPostNotFoundException(
                        AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND));
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(currentMemberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        accompanyPost.updateStatus(member.getId());
    }

    @Override
    public ReviewsResponse getReceivedReviews(Long cursorId, int size,
            Long memberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        Slice<AccompanyReview> accompanyReviews = accompanyReviewRepository.findAllByMemberAndStatus(
                cursorId, size, member, false,
                AccompanyReviewStatusType.AFTER_ACCOMPANY_AND_WRITTEN);
        List<ReviewResponse> reviewResponses = accompanyReviews.stream()
                .map(ReviewResponse::of)
                .toList();

        return ReviewsResponse.of(accompanyReviews.hasNext(), reviewResponses);
    }

    @Override
    public ReviewsResponse getWaitingReviews(Long cursorId, int size,
            Long currentMemberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(currentMemberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        Slice<AccompanyReview> accompanyReviews = accompanyReviewRepository.findAllByMemberAndStatus(
                cursorId, size, member, true,
                AccompanyReviewStatusType.AFTER_ACCOMPANY_AND_NOT_WRITTEN);
        List<ReviewResponse> reviewResponses = accompanyReviews.stream()
                .map(ReviewResponse::of)
                .toList();

        return ReviewsResponse.of(accompanyReviews.hasNext(), reviewResponses);
    }

    @Override
    public AccompanyPostsShortResponse getAccompanyPostsByMember(Long cursorId, int size,
            Long currentMemberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(currentMemberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        Slice<AccompanyPost> accompanyPosts = accompanyPostRepository.findAllByMember(cursorId,
                size, member);
        List<AccompanyPostShortResponse> accompanyPostShortResponses = accompanyPosts.stream()
                .map(AccompanyPostShortResponse::of)
                .toList();

        return AccompanyPostsShortResponse.of(accompanyPosts.hasNext(),
                accompanyPostShortResponses);
    }

    @Override
    public AccompanyCommentsShortResponse getAccompanyCommentsByMember(Long cursorId, int size,
            Long currentMemberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(currentMemberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        Slice<AccompanyComment> accompanyComments = accompanyCommentRepository.findAllByMember(
                cursorId, size, member);
        List<AccompanyCommentShortResponse> accompanyCommentShortResponses = accompanyComments.stream()
                .map(AccompanyCommentShortResponse::of)
                .toList();

        return AccompanyCommentsShortResponse.of(accompanyComments.hasNext(),
                accompanyCommentShortResponses);
    }

    private List<Member> getAccompanyConfirmedMembers(AccompanyPost accompanyPost,
            Member newCompanion) {
        List<Member> companions = new ArrayList<>();
        companions.add(newCompanion);
        companions.add(accompanyPost.getWriter());
        companions.addAll(
                accompanyReviewRepository.findDistinctReviewerAndRevieweeByAccompanyPostId(
                                accompanyPost.getId()).stream().map(companionId ->
                                memberRepository.findByIdAndIsActivatedIsTrue(companionId).orElseThrow(
                                        () -> new MemberNotFoundException(
                                                MemberErrorCode.MEMBER_NOT_FOUND)))
                        .toList());

        return companions;
    }

    private List<Member> getCompanions(AccompanyPost accompanyPost, Long currentMemberId) {
        return accompanyReviewRepository.findDistinctReviewerAndRevieweeByAccompanyPostId(
                        accompanyPost.getId()).stream()
                .filter(companionId -> !companionId.equals(currentMemberId))
                .map(companionId ->
                        memberRepository.findByIdAndIsActivatedIsTrue(companionId).orElseThrow(
                                () -> new MemberNotFoundException(
                                        MemberErrorCode.MEMBER_NOT_FOUND)))
                .toList();
    }

    private void createAccompanyReview(AccompanyPost accompanyPost, List<Member> companions) {
        List<AccompanyReview> accompanyReviews = new ArrayList<>();
        for (int i = 0; i < companions.size(); i++) {
            for (int j = i + 1; j < companions.size(); j++) {
                Member companion1 = companions.get(i);
                Member companion2 = companions.get(j);
                if (!accompanyReviewRepository.existsByCompanionsAndAccompanyPostId(
                        companion1.getId(), companion2.getId(), accompanyPost.getId())) {
                    accompanyReviews.add(AccompanyReview.builder()
                            .reviewer(companion1)
                            .reviewee(companion2)
                            .accompanyPost(accompanyPost)
                            .build());
                    accompanyReviews.add(AccompanyReview.builder()
                            .reviewer(companion2)
                            .reviewee(companion1)
                            .accompanyPost(accompanyPost)
                            .build());
                }
            }
        }

        accompanyReviewRepository.saveAll(accompanyReviews);
    }

    private void checkDuplicatedAccompanyApply(Long accompanyPostId, Long memberId) {
        if (accompanyCommentRepository.existsByAccompanyPostIdAndIsActivatedIsTrueAndMemberIdAndIsAccompanyApplyCommentTrue(
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

    private void checkConfirmerIsWriter(Long confirmerId, Long writerId) {
        if (!confirmerId.equals(writerId)) {
            throw new OnlyWriterCanConfirmApplyException(
                    AccompanyErrorCode.ONLY_WRITER_CAN_CONFIRM_APPLY);
        }
    }

    private void checkAccompanyCommentIsApplyComment(AccompanyComment accompanyComment) {
        if (!accompanyComment.getIsAccompanyApplyComment()) {
            throw new AccompanyCommentApplyConfirmDeniedException(
                    AccompanyErrorCode.ACCOMPANY_COMMENT_APPLY_CONFIRM_DENIED);
        }
    }

    private void checkAlreadyConfirmedAccompanyApplyComment(AccompanyComment accompanyComment) {
        if (accompanyComment.getIsAccompanyConfirmedComment()) {
            throw new AlreadyApplyConfirmedAccompanyCommentException(
                    AccompanyErrorCode.ALREADY_APPLY_CONFIRMED_ACCOMPANY_COMMENT);
        }
    }


}