package com.gogoring.dongoorami.accompany.repository;

import com.gogoring.dongoorami.accompany.domain.AccompanyReview;
import com.gogoring.dongoorami.accompany.domain.AccompanyReview.AccompanyReviewStatusType;
import com.gogoring.dongoorami.accompany.domain.QAccompanyReview;
import com.gogoring.dongoorami.member.domain.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class AccompanyReviewCustomRepositoryImpl implements AccompanyReviewCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QAccompanyReview accompanyReview = QAccompanyReview.accompanyReview;

    @Override
    public Slice<AccompanyReview> findAllByMemberAndStatus(Long cursorId, int size, Member member,
            Boolean isReviewer, AccompanyReviewStatusType statusType) {
        List<AccompanyReview> accompanyReviews = jpaQueryFactory
                .selectFrom(accompanyReview)
                .where(
                        memberEquals(member, isReviewer),
                        accompanyReview.status.eq(statusType),
                        accompanyReview.isActivated.isTrue(),
                        lessThanCursorId(cursorId)
                ).orderBy(accompanyReview.id.desc()).limit(size).fetch();

        boolean hasNext = false;
        if (!accompanyReviews.isEmpty()) {
            Long lastIdInResult = accompanyReviews.get(accompanyReviews.size() - 1).getId();
            hasNext = isExistByIdLessThan(lastIdInResult, member, isReviewer, statusType);
        }

        return new SliceImpl<>(accompanyReviews, Pageable.ofSize(size), hasNext);
    }

    private BooleanExpression memberEquals(Member member, Boolean isReviewer) {
        return isReviewer ? accompanyReview.reviewer.eq(member)
                : accompanyReview.reviewee.eq(member);
    }

    private BooleanExpression lessThanCursorId(Long cursorId) {
        return cursorId != null ? accompanyReview.id.lt(cursorId) : null;
    }

    private boolean isExistByIdLessThan(Long id, Member member, Boolean isReviewer,
            AccompanyReviewStatusType statusType) {
        return jpaQueryFactory.selectFrom(accompanyReview)
                .where(
                        memberEquals(member, isReviewer),
                        accompanyReview.status.eq(statusType),
                        accompanyReview.id.lt(id),
                        accompanyReview.isActivated.isTrue()
                )
                .fetchFirst() != null;
    }
}
