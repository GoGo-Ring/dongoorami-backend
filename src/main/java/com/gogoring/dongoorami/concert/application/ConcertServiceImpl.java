package com.gogoring.dongoorami.concert.application;

import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.domain.ConcertReview;
import com.gogoring.dongoorami.concert.dto.request.ConcertReviewRequest;
import com.gogoring.dongoorami.concert.dto.response.ConcertGetResponse;
import com.gogoring.dongoorami.concert.dto.response.ConcertGetShortResponse;
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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertServiceImpl implements ConcertService {

    private final ConcertRepository concertRepository;
    private final ConcertReviewRepository concertReviewRepository;
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
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        Slice<ConcertReviewGetResponse> concertReviewGetResponses = (cursorId == null
                ? concertReviewRepository.findAllByConcertAndIsActivatedIsTrueOrderByIdDesc(concert,
                PageRequest.of(0, size))
                : concertReviewRepository.findAllByIdLessThanAndConcertAndIsActivatedIsTrueOrderByIdDesc(
                        cursorId, concert, PageRequest.of(0, size))).map(
                concertReview -> ConcertReviewGetResponse.of(concertReview, member));

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

        Integer totalAccompanies = 0; // TODO: AccompanyPost와 Concert 연결 후 로직 수정
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
}
