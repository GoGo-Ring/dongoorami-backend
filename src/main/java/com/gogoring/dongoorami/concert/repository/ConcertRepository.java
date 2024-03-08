package com.gogoring.dongoorami.concert.repository;

import com.gogoring.dongoorami.concert.domain.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcertRepository extends JpaRepository<Concert, Long> {

    Boolean existsByKopisId(String kopisId);
}
