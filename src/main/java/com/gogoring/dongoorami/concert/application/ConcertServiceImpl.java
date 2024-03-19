package com.gogoring.dongoorami.concert.application;

import com.gogoring.dongoorami.accompany.domain.AccompanyReview;
import com.gogoring.dongoorami.accompany.domain.AccompanyReview.AccompanyReviewStatusType;
import com.gogoring.dongoorami.accompany.dto.response.ReviewResponse;
import com.gogoring.dongoorami.accompany.repository.AccompanyPostRepository;
import com.gogoring.dongoorami.accompany.repository.AccompanyReviewRepository;
import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.domain.ConcertReview;
import com.gogoring.dongoorami.concert.dto.request.ConcertReviewRequest;
import com.gogoring.dongoorami.concert.dto.response.ConcertGetResponse;
import com.gogoring.dongoorami.concert.dto.response.ConcertGetShortResponse;
import com.gogoring.dongoorami.concert.dto.response.ConcertInfoResponse;
import com.gogoring.dongoorami.concert.dto.response.ConcertReviewGetResponse;
import com.gogoring.dongoorami.concert.dto.response.ConcertReviewsGetResponse;
import com.gogoring.dongoorami.concert.dto.response.ConcertsGetShortResponse;
import com.gogoring.dongoorami.concert.exception.ConcertErrorCode;
import com.gogoring.dongoorami.concert.exception.ConcertNotFoundException;
import com.gogoring.dongoorami.concert.exception.ConcertReviewNotFoundException;
import com.gogoring.dongoorami.concert.repository.ConcertRepository;
import com.gogoring.dongoorami.concert.repository.ConcertReviewRepository;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.exception.MemberErrorCode;
import com.gogoring.dongoorami.member.exception.MemberNotFoundException;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertServiceImpl implements ConcertService {

    private final ConcertRepository concertRepository;
    private final ConcertReviewRepository concertReviewRepository;
    private final AccompanyPostRepository accompanyPostRepository;
    private final AccompanyReviewRepository accompanyReviewRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public void createConcertReview(Long concertId,
            ConcertReviewRequest concertReviewRequest, Long memberId) {
        Concert concert = concertRepository.findByIdAndIsActivatedIsTrue(concertId).orElseThrow(
                () -> new ConcertNotFoundException(ConcertErrorCode.CONCERT_NOT_FOUND));
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        concertReviewRepository.save(concertReviewRequest.toEntity(concert, member));
    }

    @Override
    public ConcertReviewsGetResponse getConcertReviews(Long concertId, Long cursorId, int size,
            Long memberId) {
        Concert concert = concertRepository.findByIdAndIsActivatedIsTrue(concertId).orElseThrow(
                () -> new ConcertNotFoundException(ConcertErrorCode.CONCERT_NOT_FOUND));

        Slice<ConcertReviewGetResponse> concertReviewGetResponses = (cursorId == null
                ? concertReviewRepository.findAllByConcertAndIsActivatedIsTrueOrderByIdDesc(concert,
                PageRequest.of(0, size))
                : concertReviewRepository.findAllByIdLessThanAndConcertAndIsActivatedIsTrueOrderByIdDesc(
                        cursorId, concert, PageRequest.of(0, size))).map(
                concertReview -> ConcertReviewGetResponse.of(concertReview, memberId));

        return ConcertReviewsGetResponse.of(concertReviewGetResponses.hasNext(),
                concertReviewGetResponses.getContent());
    }

    @Transactional
    @Override
    public void updateConcertReview(Long concertReviewId, ConcertReviewRequest concertReviewRequest,
            Long memberId) {
        ConcertReview concertReview = concertReviewRepository.findByIdAndIsActivatedIsTrue(
                concertReviewId).orElseThrow(() -> new ConcertReviewNotFoundException(
                ConcertErrorCode.CONCERT_REVIEW_NOT_FOUND));
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        concertReview.updateConcertReview(member.getId(), concertReviewRequest.getTitle(),
                concertReviewRequest.getContent(), concertReviewRequest.getRating());
    }

    @Transactional
    @Override
    public void deleteConcertReview(Long concertReviewId, Long memberId) {
        ConcertReview concertReview = concertReviewRepository.findByIdAndIsActivatedIsTrue(
                concertReviewId).orElseThrow(() -> new ConcertReviewNotFoundException(
                ConcertErrorCode.CONCERT_REVIEW_NOT_FOUND));
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        concertReview.updateIsActivatedFalse(member.getId());
    }

    @Override
    public ConcertGetResponse getConcert(Long concertId) {
        Concert concert = concertRepository.findByIdAndIsActivatedIsTrue(concertId).orElseThrow(
                () -> new ConcertNotFoundException(ConcertErrorCode.CONCERT_NOT_FOUND));
        Integer totalAccompanies = accompanyPostRepository.countByConcertAndIsActivatedIsTrue(
                concert);
        Integer totalReviews = concertReviewRepository.countByConcertAndIsActivatedIsTrue(concert);

        return ConcertGetResponse.of(concert, totalAccompanies, totalReviews);
    }

    @Override
    public ConcertsGetShortResponse getConcerts(Long cursorId, int size, String keyword,
            List<String> genres, List<String> statuses) {
        Slice<Concert> concerts = concertRepository.findAllByGenreAndStatus(cursorId, size, keyword,
                genres, statuses);
        List<ConcertGetShortResponse> concertGetShortResponses = concerts.stream()
                .map(ConcertGetShortResponse::of)
                .toList();

        return ConcertsGetShortResponse.of(concerts.hasNext(), concertGetShortResponses);
    }

    @Override
    public List<ConcertInfoResponse> getConcertsByKeyword(String keyword) {
        List<Concert> concerts = concertRepository.findAllByNameContaining(
                        keyword).stream()
                .filter(concert -> concert.getEndLocalDate().isAfter(LocalDate.now())).toList();

        return concerts.stream().map(ConcertInfoResponse::of).toList();
    }

    @Override
    public List<ReviewResponse> getConcertAndAccompanyReview(Long memberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<ReviewResponse> reviewResponses = concertReviewRepository.findAllByMemberAndIsActivatedIsTrue(
                member).stream().map(ReviewResponse::of).collect(Collectors.toList());
        reviewResponses.addAll(
                accompanyReviewRepository.findAllByReviewerAndStatusAndIsActivatedIsTrue(member,
                                AccompanyReviewStatusType.AFTER_ACCOMPANY_AND_WRITTEN).stream()
                        .map(ReviewResponse::of).toList());

        return reviewResponses;
    }

    @Scheduled(cron = "0 30 15 * * *", zone = "Asia/Seoul")
    @Transactional
    public void updateConcertStatus() {
        concertRepository.findAllByStatusIsNotAndIsActivatedIsTrue("공연종료")
                .forEach(concert -> {
                    concert.updateStatus();
                    if (concert.isOneDayPassedSinceEndedAt()) {
                        updateAccompanyReviewsStatus(
                                accompanyReviewRepository.findAllByConcertIdAndActivatedConcertAndActivatedAccompanyReviewAndProceedingStatus(
                                        concert.getId()));
                    }
                });
    }

    private void updateAccompanyReviewsStatus(List<AccompanyReview> accompanyReviews) {
        accompanyReviews.forEach(accompanyReview -> accompanyReview.updateStatus(
                        AccompanyReviewStatusType.AFTER_ACCOMPANY_AND_NOT_WRITTEN));
    }
}