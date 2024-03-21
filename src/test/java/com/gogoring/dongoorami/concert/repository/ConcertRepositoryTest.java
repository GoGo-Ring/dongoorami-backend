package com.gogoring.dongoorami.concert.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.gogoring.dongoorami.concert.ConcertDataFactory;
import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.exception.ConcertErrorCode;
import com.gogoring.dongoorami.concert.exception.ConcertNotFoundException;
import com.gogoring.dongoorami.global.config.QueryDslConfig;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Test
    @DisplayName("id 내림차순으로 특정 id 값 이하의 공연 목록을 조회할 수 있다.")
    void success_findAllByGenreAndStatus() {
        // given
        int size = 10;
        List<String> name = List.of("제목", "타이틀");
        List<String> genres = List.of("서양음악(클래식)", "서커스/마술");
        List<String> statuses = List.of("공연예정", "공연중");

        List<Concert> concerts = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            concerts.add(Concert.builder()
                    .name(name.get(i % 2))
                    .startedAt("2024.03.12")
                    .endedAt("2024.05.12")
                    .place("예술의전당 [서울] (리사이틀홀)")
                    .poster("http://www.kopis.or.kr/upload/pfmPoster/PF_PF236579_240304_151739.gif")
                    .genre(genres.get(i % 2))
                    .status(statuses.get(i % 2))
                    .build());
        }
        concertRepository.saveAll(concerts);

        long maxId = -1L;
        for (Concert concert : concerts) {
            maxId = Math.max(maxId, concert.getId());
        }

        // when
        Slice<Concert> slice1 = concertRepository.findAllByGenreAndStatus(null, size, null,
                List.of("서커스/마술"), null);
        Slice<Concert> slice2 = concertRepository.findAllByGenreAndStatus(maxId, size, null, null,
                null);
        Slice<Concert> slice3 = concertRepository.findAllByGenreAndStatus(maxId, size, null,
                List.of("서커스/마술", "서양음악(클래식)"), List.of("공연중"));
        Slice<Concert> slice4 = concertRepository.findAllByGenreAndStatus(maxId, size, null,
                null, List.of("공연예정", "공연중"));
        Slice<Concert> slice5 = concertRepository.findAllByGenreAndStatus(maxId, size, "타이틀",
                List.of("서양음악(클래식)"), List.of("공연예정", "공연중"));

        // then
        assertThat(slice1.getContent().stream().map(Concert::getGenre).toList())
                .doesNotContain("서양음악(클래식)");
        assertThat(slice2.getContent().size()).isEqualTo(size - 1);
        assertThat(slice3.getContent().stream().map(Concert::getStatus).toList())
                .doesNotContain("공연예정");
        assertThat(slice4.getContent().stream().map(Concert::getGenre).toList())
                .contains("서커스/마술", "서양음악(클래식)");
        assertThat(slice5.getContent().stream().map(Concert::getName).toList())
                .doesNotContain("제목");
    }

    @Test
    @DisplayName("공연종료가 아닌 상태의 공연 목록을 조회할 수 있다.")
    void success_findAllByStatusIsNotAndIsActivatedIsTrue() {
        // given
        int size = 3;
        List<Concert> concerts = ConcertDataFactory.createConcerts(size);
        concertRepository.saveAll(concerts);

        // when
        List<Concert> savedConcerts = concertRepository.findAllByStatusIsNotAndIsActivatedIsTrue(
                "공연종료");

        // then
        assertThat(savedConcerts.size()).isEqualTo(size);
    }

    @Test
    @DisplayName("특정 키워드를 이름으로 포함하는 공연 목록을 조회할 수 있다.")
    void success_findAllByNameContaining() {
        // given
        int size = 7;
        List<Concert> concerts = ConcertDataFactory.createConcerts(size);
        concertRepository.saveAll(concerts);

        // when
        List<Concert> savedConcerts = concertRepository.findAllByNameContaining("고고링");

        // then
        assertThat(savedConcerts.size()).isEqualTo(size);
    }

    @Test
    @DisplayName("공연 종료 일자 내림차순으로 공연 5개를 조회할 수 있다.")
    void success_findTop5ByIsActivatedIsTrueOrderByEndedAtDesc() {
        // given
        int size = 7;
        List<Concert> concerts = ConcertDataFactory.createConcerts(size);
        ReflectionTestUtils.setField(concerts.get(0), "endedAt", "2099.12.31");
        ReflectionTestUtils.setField(concerts.get(1), "endedAt", "2098.12.31");
        concertRepository.saveAll(concerts);

        // when
        List<Concert> savedConcerts = concertRepository.findTop5ByIsActivatedIsTrueOrderByEndedAtDesc();

        // then
        assertThat(savedConcerts.size()).isEqualTo(5);
        assertThat(savedConcerts).contains(concerts.get(0), concerts.get(1));
    }

    @Test
    @DisplayName("키워드 기반 공연 목록을 조회할 수 있다.")
    void success_findAllByKeyword() {
        // given
        int size = 3;
        List<Concert> concerts = concertRepository.saveAll(
                ConcertDataFactory.createConcerts(size * 3));
        Long concertCursorId = concerts.get(concerts.size() - 1).getId() - 1;
        String keyword = concerts.get(0).getName()
                .substring(0, concerts.get(0).getName().length() / 2);

        // when
        Slice<Concert> concertsContainingKeyword = concertRepository.findAllByKeyword(
                concertCursorId, size, keyword);

        // then
        assertThat(concertsContainingKeyword.hasNext()).isEqualTo(true);
        assertThat(concertsContainingKeyword.getContent().size()).isEqualTo(size);
    }
}
