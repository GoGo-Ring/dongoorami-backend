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
            hasNext = isExistByIdLessThan(lastIdInResult, keyword, genres, statuses);
        }

        return new SliceImpl<>(concerts, Pageable.ofSize(size), hasNext);
    }

    @Override
    public Slice<Concert> findAllByKeyword(Long cursorId, int size, String keyword) {
        List<Concert> concerts = jpaQueryFactory
                .selectFrom(concert)
                .where(
                        concertContains(keyword),
                        lessThanCursorId(cursorId),
                        concert.isActivated.eq(true)
                ).orderBy(concert.id.desc()).limit(size).fetch();

        boolean hasNext = false;
        if (!concerts.isEmpty()) {
            Long lastIdInResult = concerts.get(concerts.size() - 1).getId();
            hasNext = isExistByIdLessThan(lastIdInResult, keyword);
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

    private boolean isExistByIdLessThan(Long id, String keyword, List<String> genres,
            List<String> statuses) {
        return jpaQueryFactory.selectFrom(concert)
                .where(
                        nameLikes(keyword),
                        genresEquals(genres),
                        statusesEquals(statuses),
                        concert.id.lt(id),
                        concert.isActivated.eq(true)
                )
                .fetchFirst() != null;
    }

    private BooleanExpression concertContains(String keyword) {
        return keyword != null ?
                concert.name.like("%" + keyword + "%")
                        .or(concert.place.like("%" + keyword + "%"))
                        .or(concert.actor.like("%" + keyword + "%"))
                        .or(concert.crew.like("%" + keyword + "%"))
                        .or(concert.producer.like("%" + keyword + "%"))
                        .or(concert.agency.like("%" + keyword + "%"))
                        .or(concert.host.like("%" + keyword + "%"))
                        .or(concert.management.like("%" + keyword + "%"))
                        .or(concert.summary.like("%" + keyword + "%"))
                        .or(concert.genre.like("%" + keyword + "%")) : null;
    }

    private boolean isExistByIdLessThan(Long id, String keyword) {
        return jpaQueryFactory.selectFrom(concert)
                .where(
                        concertContains(keyword),
                        concert.id.lt(id),
                        concert.isActivated.eq(true)
                )
                .fetchFirst() != null;
    }
}
