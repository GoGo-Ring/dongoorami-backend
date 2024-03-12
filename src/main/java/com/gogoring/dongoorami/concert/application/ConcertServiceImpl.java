package com.gogoring.dongoorami.concert.application;

import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.dto.request.ConcertReviewCreateRequest;
import com.gogoring.dongoorami.concert.exception.ConcertErrorCode;
import com.gogoring.dongoorami.concert.exception.ConcertNotFoundException;
import com.gogoring.dongoorami.concert.repository.ConcertRepository;
import com.gogoring.dongoorami.concert.repository.ConcertReviewRepository;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.exception.MemberErrorCode;
import com.gogoring.dongoorami.member.exception.MemberNotFoundException;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
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
            ConcertReviewCreateRequest concertReviewCreateRequest, Long memberId) {
        Concert concert = concertRepository.findByIdAndIsActivatedIsTrue(concertId).orElseThrow(
                () -> new ConcertNotFoundException(ConcertErrorCode.CONCERT_NOT_FOUND));
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        concertReviewRepository.save(concertReviewCreateRequest.toEntity(concert, member));
    }
}
