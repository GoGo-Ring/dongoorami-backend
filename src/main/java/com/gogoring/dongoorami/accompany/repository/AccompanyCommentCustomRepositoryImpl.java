package com.gogoring.dongoorami.accompany.repository;

import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import com.gogoring.dongoorami.accompany.domain.QAccompanyComment;
import com.gogoring.dongoorami.member.domain.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class AccompanyCommentCustomRepositoryImpl implements AccompanyCommentCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QAccompanyComment accompanyComment = QAccompanyComment.accompanyComment;

    @Override
    public Slice<AccompanyComment> findAllByMember(Long cursorId, int size, Member member) {
        List<AccompanyComment> accompanyComments = jpaQueryFactory
                .selectFrom(accompanyComment)
                .where(
                        accompanyComment.member.eq(member),
                        accompanyComment.isActivated.isTrue(),
                        lessThanCursorId(cursorId)
                ).orderBy(accompanyComment.id.desc()).limit(size).fetch();

        boolean hasNext = false;
        if (!accompanyComments.isEmpty()) {
            Long lastIdInResult = accompanyComments.get(accompanyComments.size() - 1).getId();
            hasNext = isExistByIdLessThan(lastIdInResult, member);
        }

        return new SliceImpl<>(accompanyComments, Pageable.ofSize(size), hasNext);
    }

    private BooleanExpression lessThanCursorId(Long cursorId) {
        return cursorId != null ? accompanyComment.id.lt(cursorId) : null;
    }

    private boolean isExistByIdLessThan(Long id, Member member) {
        return jpaQueryFactory.selectFrom(accompanyComment)
                .where(
                        accompanyComment.member.eq(member),
                        accompanyComment.id.lt(id),
                        accompanyComment.isActivated.isTrue()
                )
                .fetchFirst() != null;
    }
}
