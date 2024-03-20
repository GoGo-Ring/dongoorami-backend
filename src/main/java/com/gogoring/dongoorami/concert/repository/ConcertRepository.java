package com.gogoring.dongoorami.concert.repository;

import com.gogoring.dongoorami.concert.domain.Concert;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcertRepository extends JpaRepository<Concert, Long>, ConcertCustomRepository {

    Boolean existsByKopisId(String kopisId);

    Optional<Concert> findByIdAndIsActivatedIsTrue(Long id);

    List<Concert> findAllByStatusIsNotAndIsActivatedIsTrue(String status);

    List<Concert> findAllByNameContaining(String keyword);

    List<Concert> findTop5ByIsActivatedIsTrueOrderByEndedAtDesc();
}
