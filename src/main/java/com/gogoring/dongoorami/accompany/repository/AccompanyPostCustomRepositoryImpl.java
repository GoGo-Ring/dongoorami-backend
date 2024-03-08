package com.gogoring.dongoorami.accompany.repository;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost.AccompanyPurposeType;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost.AccompanyRegionType;
import com.gogoring.dongoorami.accompany.domain.QAccompanyPost;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostFilterRequest;
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
        return concertPlace != null ? accompanyPost.concertPlace.eq(concertPlace) : null;
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
}
