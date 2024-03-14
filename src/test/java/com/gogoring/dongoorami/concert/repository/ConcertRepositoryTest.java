package com.gogoring.dongoorami.concert.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.gogoring.dongoorami.concert.ConcertDataFactory;
import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.exception.ConcertErrorCode;
import com.gogoring.dongoorami.concert.exception.ConcertNotFoundException;
import com.gogoring.dongoorami.global.config.QueryDslConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(QueryDslConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ConcertRepositoryTest {

    @Autowired
    private ConcertRepository concertRepository;

    @BeforeEach
    void setUp() {
        concertRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        concertRepository.deleteAll();
    }

    @Test
    @DisplayName("id로 공연을 조회할 수 있다.")
    void success_findByIdAndIsActivatedIsTrue() {
        // given
        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        // when
        Concert savedConcert = concertRepository.findByIdAndIsActivatedIsTrue(concert.getId())
                .orElseThrow(
                        () -> new ConcertNotFoundException(ConcertErrorCode.CONCERT_NOT_FOUND));

        // then
        assertThat(savedConcert.getId()).isEqualTo(concert.getId());
    }

    @Test
    @DisplayName("kopisId로 공연 존재 여부를 확인할 수 있다.")
    void success_existsByKopisId() {
        // given
        Concert concert = ConcertDataFactory.createConcert();
        concertRepository.save(concert);

        // when
        Boolean isExist = concertRepository.existsByKopisId(concert.getKopisId());

        // then
        assertThat(isExist).isEqualTo(true);
    }
}
