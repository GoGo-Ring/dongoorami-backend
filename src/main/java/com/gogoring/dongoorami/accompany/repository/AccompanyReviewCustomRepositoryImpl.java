package com.gogoring.dongoorami.accompany.repository;

import com.gogoring.dongoorami.accompany.domain.AccompanyReview;
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
    public Slice<AccompanyReview> findAllByReviewee(Long cursorId, int size, Member member) {
        List<AccompanyReview> accompanyReviews = jpaQueryFactory
                .selectFrom(accompanyReview)
                .where(
                        accompanyReview.reviewee.eq(member),
                        accompanyReview.isActivated.isTrue(),
                        lessThanCursorId(cursorId)
                ).orderBy(accompanyReview.id.desc()).limit(size).fetch();

        boolean hasNext = false;
        if (!accompanyReviews.isEmpty()) {
            Long lastIdInResult = accompanyReviews.get(accompanyReviews.size() - 1).getId();
            hasNext = isExistByIdLessThan(lastIdInResult);
        }

        return new SliceImpl<>(accompanyReviews, Pageable.ofSize(size), hasNext);
    }

    private BooleanExpression lessThanCursorId(Long cursorId) {
        return cursorId != null ? accompanyReview.id.lt(cursorId) : null;
    }

    private boolean isExistByIdLessThan(Long id) {
        return jpaQueryFactory.selectFrom(accompanyReview)
                .where(accompanyReview.id.lt(id),
                        accompanyReview.isActivated.isTrue())
                .fetchFirst() != null;
    }
}
