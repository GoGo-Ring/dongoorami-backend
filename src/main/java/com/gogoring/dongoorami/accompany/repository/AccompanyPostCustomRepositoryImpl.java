package com.gogoring.dongoorami.accompany.repository;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost.AccompanyPurposeType;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost.AccompanyRegionType;
import com.gogoring.dongoorami.accompany.domain.QAccompanyPost;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostFilterRequest;
import com.gogoring.dongoorami.member.domain.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class AccompanyPostCustomRepositoryImpl implements AccompanyPostCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QAccompanyPost accompanyPost = QAccompanyPost.accompanyPost;

    @Override
    public Slice<AccompanyPost> findByAccompanyPostFilterRequest(Long cursorId, int size,
            AccompanyPostFilterRequest accompanyPostFilterRequest) {
        List<AccompanyPost> accompanyPosts = jpaQueryFactory
                .selectFrom(accompanyPost)
                .where(
                        genderEquals(accompanyPostFilterRequest.getGender()),
                        regionEquals(accompanyPostFilterRequest.getRegion()),
                        ageOverlap(accompanyPostFilterRequest.getStartAge(),
                                accompanyPostFilterRequest.getEndAge()),
                        totalPeopleEquals(accompanyPostFilterRequest.getTotalPeople()),
                        concertPlaceEquals(accompanyPostFilterRequest.getConcertPlace()),
                        purposesEquals(accompanyPostFilterRequest.getPurposes()),
                        lessThanCursorId(cursorId),
                        accompanyPost.isActivated.eq(true)
                ).orderBy(accompanyPost.id.desc()).limit(size).fetch();
        boolean hasNext = false;
        if (!accompanyPosts.isEmpty()) {
            Long lastIdInResult = accompanyPosts.get(accompanyPosts.size() - 1).getId();
            hasNext = isExistByIdLessThan(lastIdInResult);
        }

        return new SliceImpl<>(accompanyPosts, Pageable.ofSize(size), hasNext);
    }

    @Override
    public Slice<AccompanyPost> findAllByMember(Long cursorId, int size, Member member) {
        List<AccompanyPost> accompanyPosts = jpaQueryFactory
                .selectFrom(accompanyPost)
                .where(
                        accompanyPost.writer.eq(member),
                        accompanyPost.isActivated.isTrue(),
                        lessThanCursorId(cursorId)
                ).orderBy(accompanyPost.id.desc()).limit(size).fetch();

        boolean hasNext = false;
        if (!accompanyPosts.isEmpty()) {
            Long lastIdInResult = accompanyPosts.get(accompanyPosts.size() - 1).getId();
            hasNext = isExistByIdLessThan(lastIdInResult, member);
        }

        return new SliceImpl<>(accompanyPosts, Pageable.ofSize(size), hasNext);
    }

    @Override
    public Slice<AccompanyPost> findAllByKeyword(Long cursorId, int size, String keyword) {
        List<AccompanyPost> accompanyPosts = jpaQueryFactory
                .selectFrom(accompanyPost)
                .where(
                        accompanyPostContains(keyword),
                        lessThanCursorId(cursorId),
                        accompanyPost.isActivated.eq(true)
                ).orderBy(accompanyPost.id.desc()).limit(size).fetch();

        boolean hasNext = false;
        if (!accompanyPosts.isEmpty()) {
            Long lastIdInResult = accompanyPosts.get(accompanyPosts.size() - 1).getId();
            hasNext = isExistByIdLessThan(lastIdInResult, keyword);
        }

        return new SliceImpl<>(accompanyPosts, Pageable.ofSize(size), hasNext);
    }

    private BooleanExpression genderEquals(String gender) {
        return gender != null ? accompanyPost.gender.eq(gender) : null;
    }

    private BooleanExpression regionEquals(String region) {
        return region != null ? accompanyPost.region.eq(AccompanyRegionType.getValue(region))
                : null;
    }

    private BooleanExpression ageOverlap(Long startAge, Long endAge) {
        if (startAge != null && endAge != null) {
            return accompanyPost.endAge.goe(startAge)
                    .and(accompanyPost.startAge.loe(endAge));
        }
        return null;
    }

    private BooleanExpression totalPeopleEquals(Long totalPeople) {
        return totalPeople != null ? accompanyPost.totalPeople.eq(totalPeople) : null;
    }

    private BooleanExpression concertPlaceEquals(String concertPlace) {
        return concertPlace != null ? accompanyPost.concert.place.eq(concertPlace) : null;
    }

    private BooleanExpression purposesEquals(List<String> purposes) {
        return purposes != null ? accompanyPost.purposes.any().in(
                purposes.stream()
                        .map(AccompanyPurposeType::getValue)
                        .toList()) : null;
    }

    private BooleanExpression lessThanCursorId(Long cursorId) {
        return cursorId != null ? accompanyPost.id.lt(cursorId) : null;
    }

    private boolean isExistByIdLessThan(Long id) {
        return jpaQueryFactory.selectFrom(accompanyPost)
                .where(accompanyPost.id.lt(id),
                        accompanyPost.isActivated.eq(true))
                .fetchFirst() != null;
    }

    private boolean isExistByIdLessThan(Long id, Member member) {
        return jpaQueryFactory.selectFrom(accompanyPost)
                .where(
                        accompanyPost.writer.eq(member),
                        accompanyPost.id.lt(id),
                        accompanyPost.isActivated.eq(true)
                ).fetchFirst() != null;
    }

    private BooleanExpression accompanyPostContains(String keyword) {
        return keyword != null ?
                accompanyPost.concert.name.like("%" + keyword + "%")
                        .or(accompanyPost.concert.place.like("%" + keyword + "%"))
                        .or(accompanyPost.concert.actor.like("%" + keyword + "%"))
                        .or(accompanyPost.concert.crew.like("%" + keyword + "%"))
                        .or(accompanyPost.concert.producer.like("%" + keyword + "%"))
                        .or(accompanyPost.concert.agency.like("%" + keyword + "%"))
                        .or(accompanyPost.concert.host.like("%" + keyword + "%"))
                        .or(accompanyPost.concert.management.like("%" + keyword + "%"))
                        .or(accompanyPost.concert.summary.like("%" + keyword + "%"))
                        .or(accompanyPost.concert.genre.like("%" + keyword + "%"))
                        .or(accompanyPost.title.like("%" + keyword + "%"))
                        .or(accompanyPost.content.like("%" + keyword + "%")) : null;
    }

    private boolean isExistByIdLessThan(Long id, String keyword) {
        return jpaQueryFactory.selectFrom(accompanyPost)
                .where(
                        accompanyPostContains(keyword),
                        accompanyPost.id.lt(id),
                        accompanyPost.isActivated.eq(true)
                ).fetchFirst() != null;
    }
}
