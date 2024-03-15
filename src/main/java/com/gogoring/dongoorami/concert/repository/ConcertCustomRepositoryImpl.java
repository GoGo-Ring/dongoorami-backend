package com.gogoring.dongoorami.concert.repository;

import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.domain.QConcert;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class ConcertCustomRepositoryImpl implements ConcertCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QConcert concert = QConcert.concert;

    @Override
    public Slice<Concert> findAllByGenreAndStatus(Long cursorId, int size, String keyword,
            List<String> genres, List<String> statuses) {
        List<Concert> concerts = jpaQueryFactory
                .selectFrom(concert)
                .where(
                        nameLikes(keyword),
                        genresEquals(genres),
                        statusesEquals(statuses),
                        lessThanCursorId(cursorId),
                        concert.isActivated.eq(true)
                ).orderBy(concert.id.desc()).limit(size).fetch();

        boolean hasNext = false;
        if (!concerts.isEmpty()) {
            Long lastIdInResult = concerts.get(concerts.size() - 1).getId();
            hasNext = isExistByIdLessThan(lastIdInResult);
        }

        return new SliceImpl<>(concerts, Pageable.ofSize(size), hasNext);
    }

    private BooleanExpression nameLikes(String keyword) {
        return keyword != null ? concert.name.like("%" + keyword + "%") : null;
    }

    private BooleanExpression genresEquals(List<String> genres) {
        return genres != null ? concert.genre.in(genres) : null;
    }

    private BooleanExpression statusesEquals(List<String> statuses) {
        return statuses != null ? concert.status.in(
                statuses.stream()
                        .map(status -> status.replaceAll(" ", ""))
                        .toList()) : null;
    }

    private BooleanExpression lessThanCursorId(Long cursorId) {
        return cursorId != null ? concert.id.lt(cursorId) : null;
    }

    private boolean isExistByIdLessThan(Long id) {
        return jpaQueryFactory.selectFrom(concert)
                .where(concert.id.lt(id),
                        concert.isActivated.eq(true))
                .fetchFirst() != null;
    }
}
