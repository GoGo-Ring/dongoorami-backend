package com.gogoring.dongoorami.accompany.repository;

import com.gogoring.dongoorami.accompany.domain.AccompanyReview;
import com.gogoring.dongoorami.accompany.domain.AccompanyReview.AccompanyReviewStatusType;
import com.gogoring.dongoorami.member.domain.Member;
import org.springframework.data.domain.Slice;

public interface AccompanyReviewCustomRepository {

    Slice<AccompanyReview> findAllByMemberAndStatus(Long cursorId, int size, Member member,
            Boolean isReviewer, AccompanyReviewStatusType statusType);
}
