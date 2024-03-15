package com.gogoring.dongoorami.concert.dto.response;

import com.gogoring.dongoorami.concert.domain.Concert;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ConcertGetShortResponse {

    private final Long id;

    private final String name;

    private final String place;

    private final String genre;

    private final String startedAt;

    private final String endedAt;

    private final String poster;

    private final String status;

    @Builder
    public ConcertGetShortResponse(Long id, String name, String place, String genre,
            String startedAt,
            String endedAt, String poster, String status) {
        this.id = id;
        this.name = name;
        this.place = place;
        this.genre = genre;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.poster = poster;
        this.status = status;
    }

    public static ConcertGetShortResponse of(Concert concert) {
        return ConcertGetShortResponse.builder()
                .id(concert.getId())
                .name(concert.getName())
                .place(concert.getPlace())
                .genre(concert.getGenre())
                .startedAt(concert.getStartedAt())
                .endedAt(concert.getEndedAt())
                .poster(concert.getPoster())
                .status(concert.getStatus())
                .build();
    }
}
