package com.gogoring.dongoorami.concert.repository;

import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.domain.ConcertReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcertReviewRepository extends JpaRepository<ConcertReview, Long> {

    Slice<ConcertReview> findAllByConcertAndIsActivatedIsTrueOrderByIdDesc(Concert concert,
            Pageable pageable);

    Slice<ConcertReview> findAllByIdLessThanAndConcertAndIsActivatedIsTrueOrderByIdDesc(Long id,
            Concert concert, Pageable pageable);
}
