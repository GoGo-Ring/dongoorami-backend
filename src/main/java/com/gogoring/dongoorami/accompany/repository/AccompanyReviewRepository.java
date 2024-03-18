package com.gogoring.dongoorami.accompany.repository;

import com.gogoring.dongoorami.accompany.domain.AccompanyReview;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccompanyReviewRepository extends JpaRepository<AccompanyReview, Long> {

    @Query("SELECT DISTINCT ar.reviewer.id, ar.reviewee.id FROM AccompanyReview ar WHERE ar.accompanyPost.id = :accompanyPostId")
    List<Long> findDistinctReviewerAndRevieweeByAccompanyPostId(
            @Param("accompanyPostId") Long accompanyPostId);

    @Query("SELECT CASE WHEN COUNT(ar) > 0 THEN true ELSE false END " +
            "FROM AccompanyReview ar WHERE ar.accompanyPost.id = :accompanyPostId " +
            "AND ((ar.reviewer.id = :companion1Id AND ar.reviewee.id = :companion2Id) " +
            "OR (ar.reviewer.id = :companion2Id AND ar.reviewee.id = :companion1Id))")
    boolean existsByCompanionsAndAccompanyPostId(
            @Param("companion1Id") Long companion1Id,
            @Param("companion2Id") Long companion2Id,
            @Param("accompanyPostId") Long accompanyPostId
    );

    AccompanyReview findAccompanyReviewByReviewerIdAndRevieweeIdAndAccompanyPostId(Long reviewerId,
            Long revieweeId, Long AccompanyPostId);

    @Query("SELECT AVG(ar.rating)*20 FROM AccompanyReview ar WHERE ar.reviewee.id = :revieweeId")
    Integer averageRatingPercentByRevieweeId(@Param("revieweeId") Long revieweeId);
}
