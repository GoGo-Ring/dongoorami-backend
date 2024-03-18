package com.gogoring.dongoorami.wish.repository;

import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.wish.domain.QWish;
import com.gogoring.dongoorami.wish.domain.Wish;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class WishCustomRepositoryImpl implements WishCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QWish wish = QWish.wish;

    @Override
    public Slice<Wish> findAllByMember(Long cursorId, int size, Member member) {
        List<Wish> wishes = jpaQueryFactory
                .selectFrom(wish)
                .where(
                        wish.member.eq(member),
                        lessThanCursorId(cursorId),
                        wish.isActivated.isTrue()
                ).orderBy(wish.id.desc()).limit(size).fetch();

        boolean hasNext = false;
        if (!wishes.isEmpty()) {
            Long lastIdInResult = wishes.get(wishes.size() - 1).getId();
            hasNext = isExistByIdLessThan(lastIdInResult);
        }

        return new SliceImpl<>(wishes, Pageable.ofSize(size), hasNext);
    }

    private BooleanExpression lessThanCursorId(Long cursorId) {
        return cursorId != null ? wish.id.lt(cursorId) : null;
    }

    private boolean isExistByIdLessThan(Long id) {
        return jpaQueryFactory.selectFrom(wish)
                .where(wish.id.lt(id),
                        wish.isActivated.isTrue())
                .fetchFirst() != null;
    }
}
