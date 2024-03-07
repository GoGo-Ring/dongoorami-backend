package com.gogoring.dongoorami.accompany.repository;

import com.gogoring.dongoorami.accompany.domain.QAccompanyPost;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccompanyPostCustomRepositoryImpl implements AccompanyPostCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QAccompanyPost accompanyPost = QAccompanyPost.accompanyPost;
}
