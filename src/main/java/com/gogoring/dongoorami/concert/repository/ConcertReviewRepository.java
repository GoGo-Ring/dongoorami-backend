package com.gogoring.dongoorami.concert.repository;

import com.gogoring.dongoorami.concert.domain.ConcertReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcertReviewRepository extends JpaRepository<ConcertReview, Long> {

}
