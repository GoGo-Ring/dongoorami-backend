package com.gogoring.dongoorami.accompany.repository;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccompanyPostRepository extends JpaRepository<AccompanyPost, Long>,
        AccompanyPostCustomRepository {

    List<AccompanyPost> findAllByConcertId(Long concertId);

    Slice<AccompanyPost> findAllByOrderByIdDesc(Pageable pageable);

    Slice<AccompanyPost> findByIdLessThanOrderByIdDesc(Long id, Pageable pageable);

    Optional<AccompanyPost> findByIdAndIsActivatedIsTrue(Long id);

}
