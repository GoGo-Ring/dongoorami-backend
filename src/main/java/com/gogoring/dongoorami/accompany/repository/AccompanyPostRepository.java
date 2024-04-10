package com.gogoring.dongoorami.accompany.repository;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.concert.domain.Concert;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AccompanyPostRepository extends JpaRepository<AccompanyPost, Long>,
        AccompanyPostCustomRepository {

    Integer countByConcertAndIsActivatedIsTrue(Concert concert);

    Slice<AccompanyPost> findAllByOrderByIdDesc(Pageable pageable);

    Slice<AccompanyPost> findByIdLessThanOrderByIdDesc(Long id, Pageable pageable);

    Optional<AccompanyPost> findByIdAndIsActivatedIsTrue(Long id);

    @Modifying
    @Query("UPDATE AccompanyPost ap SET ap.viewCount = ap.viewCount + 1 WHERE ap.id = :id")
    void updateViewCount(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ap from AccompanyPost ap where ap.id = :id and ap.isActivated = true")
    Optional<AccompanyPost> findByIdAndIsActivatedIsTrueForUpdate(Long id);

}
