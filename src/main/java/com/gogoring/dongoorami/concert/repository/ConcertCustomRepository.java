package com.gogoring.dongoorami.concert.repository;

import com.gogoring.dongoorami.concert.domain.Concert;
import java.util.List;
import org.springframework.data.domain.Slice;

public interface ConcertCustomRepository {

    Slice<Concert> findAllByGenreAndStatus(Long cursorId, int size, String keyword,
            List<String> genres, List<String> statuses);
}
